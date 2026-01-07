package com.journalApp.ServicesTest;


import com.journalApp.Repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

@SpringBootTest
public class UserServicesTest {

    @Autowired
    private UserRepository userRepository;


    @Test
    public void testFindByUsername(){

        assertNotNull(userRepository.findByUsername("tushar"));
    }
}
