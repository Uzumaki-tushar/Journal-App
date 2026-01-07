package com.journalApp.Controllers;


import com.journalApp.Entities.User;
import com.journalApp.Repository.JournalRepository;
import com.journalApp.Repository.UserRepository;
import com.journalApp.Services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {



    @Autowired
    private UserServices userServices;

    @GetMapping("/all-users")
    public ResponseEntity<List<User>> getAllUser() {

        List<User> users = userServices.getAllUser();

        if (users == null || users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(users, HttpStatus.OK);
    }




}
