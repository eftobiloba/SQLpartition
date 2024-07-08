package com.example.services;

import com.example.models.User;
import com.example.repos.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.example.services.JpaCrudService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JpaCrudService jpaCrudService;

    private final RestTemplate restTemplate = new RestTemplate();

    public static String generateUserId(){
        LocalDateTime now = LocalDateTime.now();
        return now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }

    public static Map<String, Object> convertUserToMap(User user){
        return objectMapper.convertValue(user, Map.class);
    }

    // Method to get random user data from randomuser.me
    public void getAndPushUser(){
        String apiUrl = "https://randomuser.me/api";
        String response = restTemplate.getForObject(apiUrl, String.class);

        try{
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode userNode = rootNode.path("results").get(0);

            String userId = generateUserId();
            String name = userNode.path("name").path("first").asText() + " " +
                    userNode.path("name").path("last").asText();

            String address = userNode.path("location").path("street").path("number").asText() + " " +
                    userNode.path("location").path("street").path("name").asText() + ", " +
                    userNode.path("location").path("state").asText() + ", " +
                    userNode.path("location").path("country").asText();

            String email = userNode.path("email").asText();
            LocalDateTime dob = LocalDateTime.parse(userNode.path("dob").path("date").asText(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            String phone = userNode.path("phone").asText();

            jpaCrudService.insertUser(name, address, email, dob, phone);
            logger.info("{} data fetched and saved successfully with userId: {}", name, userId);


        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
