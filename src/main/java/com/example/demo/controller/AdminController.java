package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.repository.BoothDetailRepository;

@Controller
public class AdminController {

    @Autowired
    BoothDetailRepository boothDetailRepository;

    // 管理者ログインページ
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // ログイン処理
    @PostMapping("/adminHome")
    public String loginAdminHome(String user_id, String password) {
        if ("admin".equals(user_id) && "kokuri1920".equals(password)) {
            return "adminhome";
        } else {
            return "login";
        }
    }

    // ブース登録画面へ
    @PostMapping("/boothmanage")
    public String boothManagePage() {
        return "boothmanage";
    }

    // ブース登録実行
    @PostMapping("/boothsubmit")
    public String boothInsert(String building, String floor, String number) {
        String boothPlace = building + "_" + floor + "_" + number;

        // booth_numberを連番で生成（例: floorとnumberを連結したint）
        int boothNumber = Integer.parseInt(floor + number);

        // 重複時スキップで登録
        boothDetailRepository.insertBoothPlace(boothNumber, boothPlace);

        return "boothOk";
    }

    // 管理画面に戻る
    @PostMapping("/backAdminHome")
    public String adminHome() {
        return "adminhome";
    }
}
