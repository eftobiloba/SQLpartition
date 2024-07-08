package com.example.components;

import com.example.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class UserScheduler {

    private static final Logger logger = LoggerFactory.getLogger(UserScheduler.class);

    @Autowired
    private UserService userService;

    @Scheduled(fixedRate = 30000) // every 30 seconds
    public void getAndPushUser(){
        logger.info("Starting to fetch and save user data");
        userService.getAndPushUser();
        logger.info("Completed fetching and saving user data");
    }
}
