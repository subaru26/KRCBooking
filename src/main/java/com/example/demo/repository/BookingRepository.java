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

    // booth 予約登録 + ID取得（PostgreSQL対応）
    public int boothBookingRegistration(String studentId, String selectDate) {
        String sql = """
            INSERT INTO booking (student_id, facilities_type, date)
            VALUES (:studentId, :facilitiesType, :date)
            RETURNING booking_id
        """;
        
        // 日付を LocalDate に変換
        LocalDate date = LocalDate.parse(selectDate);
        

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("studentId", studentId)
                .addValue("facilitiesType", "booth")
                .addValue("date", date);

        return namedParameterJdbcTemplate.queryForObject(sql, param, Integer.class);
    }

    // booking テーブルに新規レコード挿入 + ID取得
    public Integer insertBooking(String studentId, String facilitiesType, LocalDate date) {
        String sql = """
            INSERT INTO booking (student_id, facilities_type, date)
            VALUES (:student_id, :facilities_type, :date)
            RETURNING booking_id
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("student_id", studentId)
                .addValue("facilities_type", facilitiesType)
                .addValue("date", date);

        return namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
    }

    // karaoke テーブルに新規レコード挿入
    public void insertKaraoke(Integer bookingId, Integer usersNumber) {
        String sql = """
            INSERT INTO karaoke (booking_id, users_number, is_check)
            VALUES (:booking_id, :users_number, false)
        """;

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

    // 当日の有効な予約を取得（キャンセルされていない）
    public List<Map<String, Object>> findActiveBookingsByDate(LocalDate date) {
        String sql = """
            SELECT u.line_id, u.user_name, b.facilities_type,
                   bd.booth_place
            FROM booking b
            JOIN users u ON b.student_id = u.student_id
            LEFT JOIN booth bo ON b.booking_id = bo.booking_id
            LEFT JOIN booth_detail bd ON bo.booth_number = bd.booth_number
            WHERE b.date = :date
              AND (b.canceled IS NULL OR b.canceled = false)
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("date", date);

        return namedParameterJdbcTemplate.queryForList(sql, params);
    }
}
