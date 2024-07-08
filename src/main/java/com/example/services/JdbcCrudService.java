package com.example.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class JdbcCrudService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void insertUser(String name, String address,
                           String email, LocalDateTime dob, String phone) {

        String user_id = UserService.generateUserId();
        String insertQuery = "INSERT INTO user (user_id, name, address, email, dob, phone) VALUES (?,?,?,?,?,?)";

        String dateString = user_id.substring(0, 8);
        LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyyMMdd"));

        jdbcTemplate.update(insertQuery, user_id, name, address, email, dob, phone);
    }

    // Method to get row details by ID
    public Map<String, Object> getUserById(String user_id){
        String dateString = user_id.substring(0, 8);
        LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyyMMdd"));

        String query = "SELECT * FROM user WHERE user_id = ? AND date = ?";
        return jdbcTemplate.queryForMap(query, user_id, date);
    }
}
