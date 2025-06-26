package com.example.demo.controller;

import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.example.demo.repository.BoothDetailRepository;
import com.example.demo.repository.UsersRepository;
import com.example.demo.service.BoothService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class BoothBookingController {

	@Autowired
	BoothDetailRepository boothDetailRepository;

	@Autowired
	BoothService boothService;

	@Autowired
	UsersRepository usersRepository;

	@GetMapping("/calender")
	public String boothBooking(@RequestParam("lineId") String lineId, Model model) {

		String studentId = usersRepository.findStudentIdByLineId(lineId);

		model.addAttribute("studentId", studentId);

		return "boothcalender";
	}

	@PostMapping("/boothTimetable")
	public String boothTimeTable() {

		return "boothtimetable";
	}

	@PostMapping("/reservationConpleted")
	public String Booking(@RequestParam String reservationData, Model model) {

		System.out.println(reservationData);

		RestTemplate restTemplate = new RestTemplate();

		ObjectMapper objectMapper = new ObjectMapper();

		try {
			JsonNode responseData = objectMapper.readTree(reservationData);

			// 0番目の配列（時間とboothの配列）
			JsonNode timeBoothArray = responseData.get(0);

			JsonNode firstItem = timeBoothArray.get(0);

			Integer boothNumber = firstItem.get("booth").asInt();
			String boothDetail = boothDetailRepository.boothDetail(boothNumber);
			String[] boothArrayDetail = boothDetail.split("_");
			String showDetail = boothArrayDetail[0] + "号館" + boothArrayDetail[1] + boothArrayDetail[2] + "番にて";
			model.addAttribute("showDetail", showDetail);

			String startTimeText = firstItem.get("time").asText();
			LocalTime startTime = LocalTime.parse(startTimeText);

			JsonNode lastItem = timeBoothArray.get(timeBoothArray.size() - 1);
			String endTimeText = lastItem.get("time").asText();
			LocalTime endTime = LocalTime.parse(endTimeText).plusMinutes(15);

			String selectDate = responseData.get(1).get("selectedDate").asText();

			String studentId = responseData.get(2).get("studentId").asText();

			Boolean check = boothService.boothRegistration(startTime, endTime, boothNumber, studentId, selectDate);

			if (check) {

				return "reservationCompleted";

			} else {

				model.addAttribute("errorMessage", "他の方がさきに予約してしまいました。");

				return "boothcalender";
			}

		} catch (JsonProcessingException e) {
			System.out.println("JSON変換エラー: " + e.getMessage());
			e.printStackTrace();
			
			return null;
		}

	}

}