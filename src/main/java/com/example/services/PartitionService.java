package com.example.services;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;

@Service
public class PartitionService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void ensurePartitionForDate(LocalDate date) {
        String sql = "CALL AddPartition(?)";
        jdbcTemplate.update(sql, Date.valueOf(date));
    }
}
