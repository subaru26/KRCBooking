package com.example.demo.service;
import org.springframework.stereotype.Service;

@Service
public class BoothFormatService {

    // リポジトリ注入
//    private final BookingRepository bookingRepository;
//
//    public BoothFormatService(BookingRepository bookingRepository) {
//        this.bookingRepository = bookingRepository;
//    }

    // ブース情報の整形メソッド
    public static String formatBoothPlace(String boothPlace) {
        // 例: "6_1F_1" -> "6号館1階1番ブース"
        if (boothPlace == null || boothPlace.isEmpty()) {
            return "";
        }
        String[] parts = boothPlace.split("_");
        if (parts.length != 3) return boothPlace;  // フォーマット違いはそのまま返す
        return parts[0] + "号館" + parts[1].replace("F", "階") + parts[2] + "番ブース";
    }

    // リマインド送信メソッドなどもここに書くイメージ
}
