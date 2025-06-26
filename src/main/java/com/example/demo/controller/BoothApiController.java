package com.example.demo.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.BoothDetail;
import com.example.demo.model.BoothResponse;
import com.example.demo.repository.BoothDetailRepository;
import com.example.demo.repository.UsersRepository;
import com.example.demo.service.BoothService;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping("/api/booth")
public class BoothApiController {
	
	@Autowired
	BoothService boothService;
	
	@Autowired
	BoothDetailRepository boothDetailRepository;
	
	@Autowired
	UsersRepository usersRepository;
	
	//ブースがある施設を返す
	@PostMapping("/facilitys")
	public ResponseEntity<List<String>> resultFacilityList(){
		
		List<String> resultResponse = boothDetailRepository.boothFacilityList();
		
		List<String> tentativeList = new ArrayList<String>();
		
		//受け取った文字列を施設番号のみに切り出し返却リストに入れる
		for (String tentative : resultResponse) {
			
			String tentativeSlice = tentative.substring(0,1);
			
			tentativeList.add(tentativeSlice);
			
		}
		
		Set<String> tentative = new HashSet<>(tentativeList);
		
		List<String> result = new ArrayList<>(tentative);
		
		Collections.sort(result);
		
		return ResponseEntity.ok(result);
	}
	
	//ブース番号のリストを返す
	@PostMapping("/boothCount")
	public ResponseEntity<List<BoothDetail>> resultBoothList(){
		
		List<BoothDetail> result = boothDetailRepository.boothList();
		
		return ResponseEntity.ok(result);
	}
	
	//日にち受け取り予約可能時間返す
	@PostMapping("/reservationTime")
	public ResponseEntity<List<ArrayList<BoothResponse>>> ReservationAvailableTime(@RequestBody JsonNode time){
		
		String facilityNumber = time.get("selectFacility").asText();
		
		String reservationTime = time.get("selectDate").asText();
		
		List<ArrayList<BoothResponse>> result = new ArrayList<ArrayList<BoothResponse>>();
		
		result = boothService.resultBooth(reservationTime, facilityNumber);
				
		return ResponseEntity.ok(result);
	}
	
	@PostMapping("/studentCheck")
	public ResponseEntity<Boolean> usersCheck(@RequestBody JsonNode users){
		
		String Student_id = users.get("studentId").asText();
		
		return ResponseEntity.ok(usersRepository.existsByStudentId(Student_id));
		
	}
	
	

}