package com.example.demo.repository;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.model.BoothResponse;

@Repository
public class BoothRepository {

	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public BoothRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

	//予約可能開始時間返す
	public List<BoothResponse> reservationPossibleTime(String reservationTime, int boothNumber) {

		String sql = "SELECT booth.start_time, booth.end_time, booth.booth_number FROM booking "
				+ "INNER JOIN booth ON booking.booking_id = booth.booking_id "
				+ "where booking.date = :reservationTime and booth.booth_number = :boothNumber and booking.canceled = 0 order by start_time;";

		MapSqlParameterSource param = new MapSqlParameterSource()
				.addValue("reservationTime", reservationTime)
				.addValue("boothNumber", boothNumber);

		return namedParameterJdbcTemplate.query(
				sql,
				param,
				(rs, rowNum) -> {
					BoothResponse boothResponse = new BoothResponse();
					boothResponse.setStartTime(rs.getTime("start_time").toLocalTime());
					boothResponse.setEndTime(rs.getTime("end_time").toLocalTime());
					boothResponse.setBoothNumber(rs.getInt("booth_number"));
					return boothResponse;
				});
	}

	//ブース予約直前時間取得
	public List<List<String>> boothBookingTime(int booth_number, String date) {

		String sql = "select start_time, end_time from booking inner join booth "
				+ "on booking.booking_id = booth.booking_id where date = :date and booth_number = :boothNumber";

		MapSqlParameterSource params = new MapSqlParameterSource()
				.addValue("date", date)
				.addValue("boothNumber", booth_number);

		List<Map<String, Object>> rows = namedParameterJdbcTemplate.queryForList(sql, params);

		List<List<String>> result = new ArrayList<>();
		for (Map<String, Object> row : rows) {
			List<String> timePair = new ArrayList<>();
			timePair.add(String.valueOf(row.get("start_time")));
			timePair.add(String.valueOf(row.get("end_time")));
			result.add(timePair);
		}
		
		return result;
	}

	//ブース予約登録
	public void boothBoothRegistration(int bookingId, int boothNumber, LocalTime startTime, LocalTime endTime) {

		String sql = "INSERT INTO booth (booking_id, booth_number, start_time, end_time,is_check) VALUES(:bookingId, :boothNumber, :startTime, :endTime, :is_check)";

		Time startSqlTime = Time.valueOf(startTime);

		Time endSqlTime = Time.valueOf(endTime);

		MapSqlParameterSource param = new MapSqlParameterSource()
				.addValue("bookingId", bookingId)
				.addValue("boothNumber", boothNumber)
				.addValue("startTime", startSqlTime)
				.addValue("endTime", endSqlTime)
				.addValue("is_check", false);

		namedParameterJdbcTemplate.update(sql, param);

	}

}