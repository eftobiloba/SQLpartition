package com.example.services;

import com.example.models.User;
import com.example.repos.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class JpaCrudService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void insertUser(String name, String address,
                           String email, LocalDateTime dob, String phone){
        String user_id = UserService.generateUserId();
        String dateString = user_id.substring(0, 8);
        LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyyMMdd"));

        User user = new User(user_id, name, address, email, dob, phone, date);
        userRepository.save(user);
    }

    public Map<String, Object> getUserById(String user_id){
        return UserService.convertUserToMap(userRepository.findById(user_id).orElse(null));
    }

}
