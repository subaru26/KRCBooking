package com.example.demo.repository;

import java.time.LocalDate;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ClosedDayRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public ClosedDayRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    // データを登録する
    public void insert(LocalDate closeDay) {
        String sql = "INSERT INTO closed_days (close_days) VALUES (:closeDay)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("closeDay", closeDay);
        namedParameterJdbcTemplate.update(sql, params);
    }

    // すでに存在するかを確認する
    public boolean exists(LocalDate closeDay) {
        String sql = "SELECT COUNT(*) FROM closed_days WHERE close_days = :closeDay";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("closeDay", closeDay);
        Integer count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }
}