package com.example.demo.repository;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Karaoke;

@Repository

public class KaraokeRepository {
	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public KaraokeRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

	public void saveKaraoke(Karaoke karaoke, int bookingId) {
	    String sql = "INSERT INTO karaoke (booking_id, users_number, is_check) " +
	                 "VALUES (:booking_id, :users_number, :is_check)";

	    MapSqlParameterSource params = new MapSqlParameterSource()
	        .addValue("booking_id", bookingId)
	        .addValue("user_number", karaoke.getUserNumber())
	        .addValue("is_check", karaoke.getIsCheck());

	    namedParameterJdbcTemplate.update(sql, params);
	}
	// 予約可能な日を取得するメソッド
	public String getAvailableDate() {
		// ここではダミーの日付を返す
		return "2023-10-01";
	}
	
	
}
