package com.journalApp.Scheduler;

import com.journalApp.Cache.AppCache;
import com.journalApp.Entities.JournalEntity;
import com.journalApp.Entities.User;
import com.journalApp.Repository.UserRepositoryImpl;
import com.journalApp.Services.EmailService;
import com.journalApp.Services.SentimentAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserSchedular {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepositoryImpl userRepository;

    @Autowired
    private SentimentAnalysisService sentimentAnalysisService;

    @Autowired
    private AppCache appCache;


    @Scheduled(cron = "0 9 * * SUN")
    public void fetchUsersAndSendMail(){
        List<User> users= userRepository.getUserForSA();

        for(User user:users){
            List<String> filteredEntries = user.getJournalEntities().stream().filter(x-> x.getDate().isAfter(LocalDateTime.now().minusDays(7))).map(JournalEntity::getContent).toList();
            String entry = String.join(" ",filteredEntries);

            String sentiment= sentimentAnalysisService.getSentiment(entry);

            emailService.sendEmail(user.getEmail(),"Sentiment for last 7 days",sentiment);
        }
    }

    @Scheduled(cron="*/5 * * * *")
    public void clearAppCache(){
        appCache.init();
    }


}
