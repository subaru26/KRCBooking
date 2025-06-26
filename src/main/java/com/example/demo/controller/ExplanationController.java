package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ExplanationController {
	
	
	@GetMapping("/explanation")
	public String explanation() {
		
		return "explanation";
		
	}

}
