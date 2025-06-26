package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.UsersRepository;

@Controller
public class ReservationHistoryController {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * LINEのユーザーIDから予約履歴を取得してHTMLで表示する
     */
    @GetMapping("/reservationhistory")
    public String getReservationHistory(@RequestParam("lineId") String lineId, Model model) {
        // lineIdからstudent_idを取得
    	model.addAttribute("lineId", lineId);
        String studentId = usersRepository.findStudentIdByLineId(lineId);
        System.out.println("lineId: " + lineId);
        if (studentId == null) {
            model.addAttribute("errorMessage", "ユーザーが見つかりません。");
            return "reservationHistory";
        }

        // student_idから予約詳細リストを取得
        List<Map<String, Object>> bookings = bookingRepository.findDetailedBookingsByStudentId(studentId);
        if (bookings == null || bookings.isEmpty()) {
            model.addAttribute("message", "予約履歴はありません。");
        } else {
            model.addAttribute("bookings", bookings);
        }

        return "reservationHistory"; // src/main/resources/templates/reservationHistory.html を返す
    }

    /**
     * 予約キャンセル処理
     */
    @PostMapping("/cancel")
    public String cancelBooking(
            @RequestParam("bookingId") Long bookingId,
            @RequestParam("lineId") String lineId,
            Model model) {

        // 1. 予約の存在確認
        String checkSql = "SELECT COUNT(*) FROM booking WHERE booking_id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, bookingId);

        if (count == null || count == 0) {
            model.addAttribute("errorMessage", "予約が見つかりませんでした。");
            return "reservationHistory";
        }

        // 2. booking テーブルの canceled を true に更新（論理削除）
        String updateSql = "UPDATE booking SET canceled = true WHERE booking_id = ?";
        jdbcTemplate.update(updateSql, bookingId);

        // 3. 再度予約一覧を取得して表示用データをセット
        String studentId = usersRepository.findStudentIdByLineId(lineId);
        if (studentId != null) {
            List<Map<String, Object>> bookings = bookingRepository.findDetailedBookingsByStudentId(studentId);
            if (bookings == null || bookings.isEmpty()) {
                model.addAttribute("message", "予約履歴はありません。");
            } else {
                model.addAttribute("bookings", bookings);
            }
        }

        // 4. メッセージとlineIdを渡す（戻るボタン用）
        model.addAttribute("message", "予約をキャンセルしました。");
        model.addAttribute("lineId", lineId);

        return "reservationHistory";
    }
}