package com.example.controllers;
import com.example.models.User;
import com.example.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.services.JpaCrudService;
import com.example.services.JdbcCrudService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    private final JpaCrudService jpaCrudService = new JpaCrudService();
    @Autowired
    private final JdbcCrudService jdbcCrudService = new JdbcCrudService();

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/jpa/users/new")
    public void insertUserJPA(@RequestBody Map<String, String> body){
        jpaCrudService.insertUser(body.get("name"), body.get("address"), body.get("email"), LocalDateTime.parse(body.get("dob")), body.get("phone"));
    }

    @GetMapping("/jpa/users/one/")
    public Map<String, Object> getUserDetailsJPA(@RequestParam String user_id){
        return jpaCrudService.getUserById(user_id);
    }

    @PostMapping("/jdbc/users/new")
    public void insertUserJDBC(@RequestBody Map<String, String> body){
        jdbcCrudService.insertUser(body.get("name"), body.get("address"), body.get("email"), LocalDateTime.parse(body.get("dob")), body.get("name"));
    }

    @GetMapping("/jdbc/users/one/")
    public Map<String, Object> getUserDetailsJDBC(@RequestParam String user_id){
        return jdbcCrudService.getUserById(user_id);
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/latest-user")
    public Optional<User> getLatestUser() {
        return userRepository.findTopByOrderByUserIdDesc();
    }
}

