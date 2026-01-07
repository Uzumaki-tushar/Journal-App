package com.journalApp.Services;

import com.journalApp.Entities.JournalEntity;
import com.journalApp.Entities.User;
import com.journalApp.Repository.JournalRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class JournalServices {

    private static final Logger logger = LoggerFactory.getLogger(JournalServices.class);

    @Autowired
    private JournalRepository journalRepository;

    @Autowired
    private UserServices userServices;

    @Transactional
    public JournalEntity saveEntry(JournalEntity journalEntity, String username) {

        logger.info("Saving journal entry for user: {}", username);

        journalEntity.setDate(LocalDateTime.now());
        JournalEntity savedJournal = journalRepository.save(journalEntity);

        User user = userServices.findByUsername(username);
        if (user == null) {
            logger.error("User not found while saving journal: {}", username);
            throw new RuntimeException("User not found");
        }

        user.getJournalEntities().add(savedJournal);
        userServices.saveUser(user);

        logger.info("Journal entry saved successfully. JournalId: {}", savedJournal.getId());

        return savedJournal;
    }

    public List<JournalEntity> getAllJournal() {
        logger.info("Fetching all journal entries");
        return journalRepository.findAll();
    }

    public Optional<JournalEntity> getJournalId(ObjectId id, User user) {

        if (user == null) {
            logger.warn("Attempt to fetch journal with null user. JournalId: {}", id);
            return Optional.empty();
        }

        logger.info("Fetching journal with id {} for user {}", id, user.getUsername());

        return findJournalByUser(user).stream()
                .filter(journal -> id.equals(journal.getId()))
                .findFirst();
    }

    @Transactional
    public void deleteJournal(ObjectId journalId, String username) {

        logger.info("Deleting journal {} for user {}", journalId, username);

        User user = userServices.findByUsername(username);
        if (user == null) {
            logger.error("User not found while deleting journal: {}", username);
            throw new RuntimeException("User not found");
        }

        boolean removed = user.getJournalEntities()
                .removeIf(journal -> journal.getId().equals(journalId));

        if (!removed) {
            logger.warn("Journal {} does not belong to user {}", journalId, username);
            throw new RuntimeException("Journal does not belong to this user");
        }

        userServices.saveUser(user);
        journalRepository.deleteById(journalId);

        logger.info("Journal {} deleted successfully for user {}", journalId, username);
    }

    public void updateJournal(JournalEntity journalEntity) {
        logger.info("Updating journal entry with id {}", journalEntity.getId());
        journalRepository.save(journalEntity);
    }

    public List<JournalEntity> findJournalByUser(User user) {

        if (user == null) {
            logger.warn("Attempt to fetch journals with null user");
            return Collections.emptyList();
        }

        logger.info("Fetching journals for user {}", user.getUsername());

        List<JournalEntity> journals = user.getJournalEntities();
        return journals != null ? journals : Collections.emptyList();
    }
}
