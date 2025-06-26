package com.example.demo.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.repository.ClosedDayRepository;

@Controller
public class CsvUploadController {

	@Autowired
	ClosedDayRepository closedDayRepository;

	@PostMapping("/uploadForm")
	public String showUploadForm() {
		return "uploadForm"; // アップロードフォームのテンプレート名
	}

	@PostMapping("/closedcreate")
	public String handleCsvUpload(@RequestParam("csvFile") MultipartFile file) {
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

			String line;
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/M/d"); // 柔軟な日付形式を許容

			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (!line.isEmpty()) {
					try {
						LocalDate date = LocalDate.parse(line, formatter);

						if (!closedDayRepository.exists(date)) {
							closedDayRepository.insert(date);
						}

					} catch (DateTimeParseException e) {
						System.err.println("日付パースエラー: " + line);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "error"; // エラー画面へ
		}

		return "redirect:/success"; // 成功画面へリダイレクト
	}

	@PostMapping("/success")
	public String showSuccess() {
		return "success"; // 成功画面のテンプレート名
	}
}
