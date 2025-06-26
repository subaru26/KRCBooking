package com.example.demo.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.KaraokeRepository;
import com.example.demo.repository.UsersRepository;

@Controller
public class KaraokeBookingController {

    @Autowired
    private KaraokeRepository karaokeRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UsersRepository usersRepository;

    /**
     * カラオケ予約ページ表示（カレンダー + フォーム）
     */
    @GetMapping("/karaokeBooking")
    public String showBookingPage(@RequestParam("lineId") String lineId, Model model) {
        // lineIdからstudent_idを取得
        model.addAttribute("lineId", lineId);
        String studentId = usersRepository.findStudentIdByLineId(lineId);
        model.addAttribute("studentId", studentId);

        // 満杯の日付を取得（例：10人以上予約されている日）
        List<LocalDate> fullDates = bookingRepository.findFullDates();
        List<String> fullDateStrings = fullDates.stream()
                .map(LocalDate::toString) // yyyy-MM-dd形式
                .collect(Collectors.toList());

        model.addAttribute("fullDates", fullDateStrings);
        return "karaokeBooking";
    }

    /**
     * カラオケ予約送信処理
     */
    @PostMapping("/karaokeBooking")
    public String handleBooking(
            @RequestParam("student_id") String studentId,
            @RequestParam("user_number") Integer userNumber,
            @RequestParam("selected_date") String selectedDate,
            Model model,
            RedirectAttributes redirectAttributes) {

        // 学籍番号を再度 model に渡してフォームに表示させる
        model.addAttribute("studentId", studentId);

        // 学籍番号の存在チェック
        if (!usersRepository.existsByStudentId(studentId)) {
            model.addAttribute("errorMessage", "登録されていない学籍番号です");
            model.addAttribute("user_number", userNumber);
            model.addAttribute("selected_date", selectedDate);

            // 満杯の日付も再度渡す（再表示時に必要）
            List<LocalDate> fullDates = bookingRepository.findFullDates();
            List<String> fullDateStrings = fullDates.stream()
                    .map(LocalDate::toString)
                    .collect(Collectors.toList());
            model.addAttribute("fullDates", fullDateStrings);

            return "karaokeBooking";
        }

        // 日付の入力チェックと変換
        LocalDate date;
        try {
            if (selectedDate == null || selectedDate.isEmpty()) {
                model.addAttribute("errorMessage", "日付が入力されていません");
                model.addAttribute("user_number", userNumber);
                model.addAttribute("selected_date", selectedDate);
                return "karaokeBooking";
            }
            date = LocalDate.parse(selectedDate);
        } catch (DateTimeParseException e) {
            model.addAttribute("errorMessage", "日付の形式が不正です");
            model.addAttribute("user_number", userNumber);
            model.addAttribute("selected_date", selectedDate);
            return "karaokeBooking";
        }

        // 登録処理
        Integer bookingId = bookingRepository.insertBooking(studentId, "karaoke", date);
        bookingRepository.insertKaraoke(bookingId, userNumber);

        // 完了画面表示のためにフラッシュスコープへ保存
        redirectAttributes.addFlashAttribute("studentId", studentId);
        redirectAttributes.addFlashAttribute("userNumber", userNumber);
        redirectAttributes.addFlashAttribute("date", date);

        return "redirect:/karaokeBookingComplete";
    }

    /**
     * カラオケ予約完了画面表示
     */
    @GetMapping("/karaokeBookingComplete")
    public String showBookingCompletePage() {
        return "karaokeBookingComplete";
    }
}
