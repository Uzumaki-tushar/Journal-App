package com.journalApp.Controllers;

import com.journalApp.ApiResponse.WeatherResponse;
import com.journalApp.Entities.User;
import com.journalApp.Services.UserServices;
import com.journalApp.Services.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private  UserServices userServices;

    @Autowired
    private WeatherService weatherService;



    @PostMapping("/create")
    public ResponseEntity<User> createEntry(@RequestBody User user){

        try {
            userServices.saveNewUser(user);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }




    @PutMapping("/updateEntry")
    public ResponseEntity<User> updateEntry(@RequestBody User user) {

        try {
            // 1. Get Authentication from SecurityContext
            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();

            // 2. Get logged-in username
            String username = authentication.getName();

            // 3. Fetch user from DB
            User dbUser = userServices.findByUsername(username);

            if (dbUser == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // 4. Update allowed fields only
            dbUser.setPassword(user.getPassword());

            userServices.saveNewUser(dbUser);

            return new ResponseEntity<>(dbUser, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }





    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteLoggedInUser() {

        try {
            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();

            String username = authentication.getName();

            userServices.deleteUserAndJournals(username);

            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/greetings")
    public ResponseEntity<?> greeting(){
        try{
            Authentication authentication=SecurityContextHolder.getContext().getAuthentication();

            WeatherResponse weatherResponse= weatherService.getWeather("Mumbai");
            String greetings="";
            if(weatherResponse!=null) {
                greetings=" Weather feels like " +weatherResponse.getCurrent().getFeelslike();

            }
            return new ResponseEntity<>("Hi " + authentication.getName() +greetings, HttpStatus.OK);

        }
        catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


}
