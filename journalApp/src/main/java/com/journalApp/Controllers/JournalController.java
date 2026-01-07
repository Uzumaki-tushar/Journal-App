package com.journalApp.Controllers;

import com.journalApp.Entities.JournalEntity;
import com.journalApp.Entities.User;
import com.journalApp.Services.JournalServices;
import com.journalApp.Services.UserServices;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/journal")
public class JournalController {

    @Autowired
    private JournalServices  journalServices;

    @Autowired
    private UserServices userServices;

    @GetMapping("/findAll")
    public ResponseEntity<List<JournalEntity>> getAllJournalByUser() {

        // 1. Get logged-in user from SecurityContext
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();
//        System.out.println(username);

        // 2. Fetch user from DB
        User user = userServices.findByUsername(username);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // 3. Get journals
        List<JournalEntity> allJournals = user.getJournalEntities();

        if (allJournals == null || allJournals.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(allJournals, HttpStatus.OK);
    }


    @PostMapping("/create")
    public ResponseEntity<JournalEntity> createEntry(@RequestBody JournalEntity journalEntity) {

        try {
            // 1. Get logged-in user from SecurityContext
            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();

            String username = authentication.getName();

            // 2. Save journal for logged-in user
            JournalEntity savedJournalEntity =
                    journalServices.saveEntry(journalEntity, username);

            return new ResponseEntity<>(savedJournalEntity, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/findEntry/{id}")
    public ResponseEntity<JournalEntity> findEntry(@PathVariable ObjectId id) {

        // Get logged-in user
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        User user = userServices.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Fetch journal owned by user
        Optional<JournalEntity> journal =
                journalServices.getJournalId(id, user);

        return journal
                .map(j -> ResponseEntity.ok(j))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @DeleteMapping("/deleteEntry/{id}")
    public ResponseEntity<Void> deleteEntry(@PathVariable ObjectId id) {

        try {
            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();

            String username = authentication.getName();

            journalServices.deleteJournal(id, username);

            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }




    @PutMapping("/updateEntry/{id}")
    public ResponseEntity<JournalEntity> updateEntry(
            @PathVariable ObjectId id,
            @RequestBody JournalEntity journalEntity) {

        try {
            Authentication auth =
                    SecurityContextHolder.getContext().getAuthentication();

            String username = auth.getName();
            User user = userServices.findByUsername(username);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Optional<JournalEntity> existingJournal =
                    journalServices.getJournalId(id, user);

            if (existingJournal.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            JournalEntity curr = existingJournal.get();

            if (journalEntity.getTitle() != null && !journalEntity.getTitle().isBlank()) {
                curr.setTitle(journalEntity.getTitle());
            }
            if (journalEntity.getContent() != null && !journalEntity.getContent().isBlank()) {
                curr.setContent(journalEntity.getContent());
            }

            journalServices.updateJournal(curr);

            return ResponseEntity.ok(curr);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }



}
