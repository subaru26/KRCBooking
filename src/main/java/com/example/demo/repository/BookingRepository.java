package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BookingRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public BookingRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }
public int boothBookingRegistration(String studentId, String selectDate) {
		
		String sql = "INSERT INTO booking (student_id, facilities_type, date) VALUES(:studentId, :facilitiesType, :date)";
		
		MapSqlParameterSource param = new MapSqlParameterSource()
				.addValue("studentId", studentId)
				.addValue("facilitiesType", "booth")
				.addValue("date", selectDate);
		
		namedParameterJdbcTemplate.update(sql, param);
		
		return namedParameterJdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", new MapSqlParameterSource(), Integer.class);
	}


//    // 選択された日付の予約idのリストを返す
//    public List<Integer> boothBookingNumber(String reservationTime) {
//        String sql = "SELECT booking_id FROM booking WHERE date = :date";
//
//        MapSqlParameterSource params = new MapSqlParameterSource()
//                .addValue("date", reservationTime);
//
//        return namedParameterJdbcTemplate.queryForList(sql, params, Integer.class);
//    }

    // booking テーブルに新規レコード挿入
    public Integer insertBooking(String studentId, String facilitiesType, LocalDate date) {
        String sql = "INSERT INTO booking (student_id, facilities_type, date) VALUES (:student_id, :facilities_type, :date)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("student_id", studentId)
                .addValue("facilities_type", facilitiesType)
                .addValue("date", date);

        namedParameterJdbcTemplate.update(sql, params);

        return namedParameterJdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", new MapSqlParameterSource(), Integer.class);
    }

    // karaoke テーブルに新規レコード挿入
    public void insertKaraoke(Integer bookingId, Integer usersNumber) {
        String sql = "INSERT INTO karaoke (booking_id, users_number, is_check) VALUES (:booking_id, :users_number, false)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("booking_id", bookingId)
                .addValue("users_number", usersNumber)
                .addValue("is_check", false);

        namedParameterJdbcTemplate.update(sql, params);
    }

    // 1日1枠制。予約済みの日付リストを取得
    public List<LocalDate> findFullDates() {
        String sql = """
            SELECT DISTINCT b.date
            FROM booking b
            WHERE b.facilities_type = 'karaoke'
              AND (b.canceled IS NULL OR b.canceled = false)
        """;

        return namedParameterJdbcTemplate.queryForList(sql, new MapSqlParameterSource(), LocalDate.class);
    }

//    // LINEのユーザーIDから予約情報を取得（karaoke込み）
//    public List<Map<String, Object>> findBookingsByLineId(String lineId) {
//        String sql = """
//            SELECT 
//                b.booking_id, 
//                b.date, 
//                b.facilities_type, 
//                k.users_number, 
//                k.is_check,
//                b.canceled
//            FROM users u
//            JOIN booking b ON u.student_id = b.student_id
//            LEFT JOIN karaoke k ON b.booking_id = k.booking_id
//            WHERE u.line_id = :line_id
//            ORDER BY b.date DESC
//        """;
//
//        MapSqlParameterSource params = new MapSqlParameterSource()
//                .addValue("line_id", lineId);
//
//        return namedParameterJdbcTemplate.queryForList(sql, params);
//    }

    // student_id から予約情報をユーザー名・人数込みで取得
    public List<Map<String, Object>> findDetailedBookingsByStudentId(String studentId) {
        String sql = """
            SELECT 
                b.booking_id, 
                b.date, 
                CASE 
                    WHEN b.facilities_type = 'karaoke' THEN '4号館カラオケ'
                    WHEN b.facilities_type = 'booth' THEN bd.booth_place
                    ELSE b.facilities_type
                END AS facilities_type,
                u.user_name, 
                k.users_number,
                b.canceled
            FROM booking b
            JOIN users u ON b.student_id = u.student_id
            LEFT JOIN karaoke k ON b.facilities_type = 'karaoke' AND b.booking_id = k.booking_id
            LEFT JOIN booth bo ON b.facilities_type = 'booth' AND b.booking_id = bo.booking_id
            LEFT JOIN booth_detail bd ON bo.booth_number = bd.booth_number
            WHERE b.student_id = :student_id
            ORDER BY b.date DESC
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("student_id", studentId);

        return namedParameterJdbcTemplate.queryForList(sql, params);
    }

//    // --- 新規追加: キャンセル（論理削除） ---
//    public int cancelBookingById(Integer bookingId) {
//        String sql = "UPDATE booking SET is_canceled = true WHERE booking_id = :booking_id";
//        MapSqlParameterSource params = new MapSqlParameterSource()
//                .addValue("booking_id", bookingId);
//
//        return namedParameterJdbcTemplate.update(sql, params);
//    }

//    // --- 物理削除する場合はこちら ---
//    public int deleteBookingById(Integer bookingId) {
//        String sql = "DELETE FROM booking WHERE booking_id = :booking_id";
//        MapSqlParameterSource params = new MapSqlParameterSource()
//                .addValue("booking_id", bookingId);
//
//        return namedParameterJdbcTemplate.update(sql, params);
//    }
 // 当日の有効な予約を取得（キャンセルされていない）
    public List<Map<String, Object>> findActiveBookingsByDate(LocalDate date) {
        String sql = """
            SELECT u.line_id, u.user_name, b.facilities_type,
                   bd.booth_place
            FROM booking b
            JOIN users u ON b.student_id = u.student_id
            LEFT JOIN booth bo ON b.booking_id = bo.booking_id
            LEFT JOIN booth_detail bd ON bo.booth_number = bd.booth_number
            WHERE b.date = :date AND b.canceled = 0
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("date", date);

        return namedParameterJdbcTemplate.queryForList(sql, params);
    }

    
}