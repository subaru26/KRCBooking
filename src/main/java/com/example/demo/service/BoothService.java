package com.example.demo.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.BoothResponse;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.BoothDetailRepository;
import com.example.demo.repository.BoothRepository;

@Service
public class BoothService {

	@Autowired
	BookingRepository bookingRepository;
	
	@Autowired
	BoothDetailRepository boothDetailRepository;

	@Autowired
	BoothRepository boothRepository;

	//予約可能時間検索
	public List<ArrayList<BoothResponse>> resultBooth(String reservationTime, String facilityNumber) {

		LocalTime openingTime = LocalTime.of(9, 00);
		LocalTime closingTime = LocalTime.of(17, 0);

		int boothNum = Integer.parseInt(facilityNumber) + 1;
		
		System.out.println(boothNum);
		
		List<Integer> boothNumbers = boothDetailRepository.boothNumbers(boothNum);
		
		List<ArrayList<BoothResponse>> result = new ArrayList<ArrayList<BoothResponse>>();

		for (int useBoothNum : boothNumbers) {

			// 指定日の予約済み時間帯を取得（start_time, end_time）
			List<BoothResponse> reserved = boothRepository.reservationPossibleTime(reservationTime, useBoothNum);

			ArrayList<BoothResponse> BoothResponse = new ArrayList<>();

			LocalTime current = openingTime;

			for (BoothResponse b : reserved) {
				if (b.getStartTime().isAfter(current)) {
					BoothResponse.add(new BoothResponse(current, b.getStartTime(), b.getBoothNumber()));
				}
				current = b.getEndTime().isAfter(current) ? b.getEndTime() : current;
			}

			// 最後の予約のあとに空きがある場合
			if (current.isBefore(closingTime)) {
				BoothResponse.add(new BoothResponse(current, closingTime, useBoothNum));
			}
			
			result.add(BoothResponse);

		}
		return result;
	}

	//予約登録
	public Boolean boothRegistration(LocalTime startTime, LocalTime endTime, int boothNumber, String studentId,
			String selectDate) {

		//事前チェック
		List<List<String>> timeList = boothRepository.boothBookingTime(boothNumber, selectDate);

		for (List<String> oenTimeLine : timeList) {

			LocalTime bookedStart = LocalTime.parse(oenTimeLine.get(0));
			LocalTime bookedEnd = LocalTime.parse(oenTimeLine.get(1));

			// 重なってたら false を返す（終了 <= 開始 でなければかぶってる）
			if (!(endTime.isBefore(bookedStart) || startTime.isAfter(bookedEnd))) {
				return false;
			}
		}

		//bookingテーブル追加
		int bookingNumber = bookingRepository.boothBookingRegistration(studentId, selectDate);

		boothRepository.boothBoothRegistration(bookingNumber, boothNumber, startTime, endTime);

		return true;

	}

}