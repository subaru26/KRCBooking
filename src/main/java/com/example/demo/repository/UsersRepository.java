package com.example.demo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Users;

@Repository
public class UsersRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    // student_id が users テーブルに存在するかを確認
    public boolean existsByStudentId(String studentId) {
        String sql = "SELECT COUNT(*) FROM users WHERE student_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, studentId);
        return count != null && count > 0;
    }

    // Users エンティティを保存する
    public void save(Users user) {
        String sql = "INSERT INTO users (student_id, user_name, line_id) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, user.getStudent_id(), user.getUserName(), user.getLineId());
    }

    // line_id から student_id を取得する
    public String findStudentIdByLineId(String lineId) {
        String sql = "SELECT student_id FROM users WHERE line_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, lineId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // line_id から Users エンティティを取得する（追加メソッド）
    public Users findByLineId(String lineId) {
        String sql = "SELECT * FROM users WHERE line_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Users.class), lineId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
}