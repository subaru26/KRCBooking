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

	//管理者ログイン関係////////////////////////////////////////////
	@GetMapping("/login")
	public String loginPage() {
		return "login";
	}

	@PostMapping("/adminHome")
	public String loginAdminHome(String user_id, String password) {

		if (user_id.equals("admin") && password.equals("kokuri1920")) {
			return "adminhome";
		} else {
			return "login";
		}
	}

	//////ブース登録//////////////

	@PostMapping("/boothmanage")
	public String boothManagePage() {
		return "boothmanage";
	}

	@PostMapping("/boothsubmit")
	public String boothInsert(String building, String floor, String number) {

		String boothPlace = building + "_" + floor + "_" + number;

		boothDetailRepository.insertBoothPlace(boothPlace);

		return "boothOk";
	}
	/////////////////////////////


	@PostMapping("/backAdminHome")
	public String adminHome() {
		return "adminhome";
	}

}
