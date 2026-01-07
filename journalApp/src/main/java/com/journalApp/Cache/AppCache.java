package com.journalApp.Cache;

import com.journalApp.Entities.ConfigJournalAppEntity;
import com.journalApp.Repository.ConfigJournalAppRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AppCache {

    @Autowired
    private ConfigJournalAppRepository configJournalAppRepository;


   public Map<String,String> cache= new HashMap<>();

    @PostConstruct
    public void init(){
        List<ConfigJournalAppEntity> all = configJournalAppRepository.findAll();
        for(ConfigJournalAppEntity configJournalAppEntity:all){
            cache.put(configJournalAppEntity.getKey(),configJournalAppEntity.getValue());
        }



    }

    public String get(String key) {
        return cache.get(key);
    }
}
