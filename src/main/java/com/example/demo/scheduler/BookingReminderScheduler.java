package com.example.demo.scheduler;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.demo.repository.BookingRepository;
import com.example.demo.service.BoothFormatService;
import com.example.demo.service.LineNotifyService;

@Component
public class BookingReminderScheduler {

    private final BookingRepository bookingRepository;
    private final LineNotifyService lineNotifyService;

    public BookingReminderScheduler(BookingRepository bookingRepository,
                                    LineNotifyService lineNotifyService) {
        this.bookingRepository = bookingRepository;
        this.lineNotifyService = lineNotifyService;
    }

    // 毎朝9時に、当日の予約者にLINEリマインド通知を送信
    @Scheduled(cron = "0 0 9 * * *")
    public void sendTodayReminders() {
        LocalDate today = LocalDate.now();
        

        List<Map<String, Object>> bookings = bookingRepository.findActiveBookingsByDate(today);

        for (Map<String, Object> booking : bookings) {
            String lineId = (String) booking.get("line_id");
            String userName = (String) booking.get("user_name");
            String facility = (String) booking.get("facilities_type");
            String boothPlace = (String) booking.get("booth_place");
            // ブースの場所をフォーマット
			if (boothPlace != null) {
				boothPlace = BoothFormatService.formatBoothPlace(boothPlace);
			}
            

            String message = String.format("%sさん、本日「%s」の予約があります！時間に余裕をもってご利用ください。",
                    userName, facility.equals("karaoke") ? "4号館カラオケ" : boothPlace);

            lineNotifyService.pushMessage(lineId, message);
        }
    }
}
