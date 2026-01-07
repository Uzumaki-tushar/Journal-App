package com.journalApp.Services;

import com.journalApp.Entities.User;
import com.journalApp.Repository.JournalRepository;
import com.journalApp.Repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServices {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JournalRepository journalRepository;

    public void saveNewUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public void saveUser(User user){
        userRepository.save(user);
    }

    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    public Optional<User> getUserId(ObjectId id) {
        return userRepository.findById(id);
    }

    public void deleteUser(ObjectId id) {
        userRepository.deleteById(id);
    }

    public User findByUsername(String userName) {
        return userRepository.findByUsername(userName);
    }

    public void deleteByUsername(String username) {
        userRepository.deleteUserByUsername(username);
    }


    @Transactional
    public void deleteUserAndJournals(String username) {

        // 1. Find user
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // 2. Delete all journals of user
        if (user.getJournalEntities() != null && !user.getJournalEntities().isEmpty()) {
            journalRepository.deleteAll(user.getJournalEntities());
        }

        // 3. Delete user
        userRepository.delete(user);
    }
}
