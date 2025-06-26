package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.Users;
import com.example.demo.repository.UsersRepository;

@Controller
public class CreateUserController {

    @Autowired
    private UsersRepository usersRepository;

    @GetMapping("/createForm")
    public String createUser(@RequestParam("lineId") String lineId, Model model) {
        model.addAttribute("lineId", lineId);
        return "createForm";
    }

    @PostMapping("/createSuccess")
    public String postUser(
            @RequestParam("student_id") String studentId,
            @RequestParam("user_name") String userName,
            @RequestParam("line_id") String lineId,
            Model model
    ) {
    	// 既に登録されていたらスキップなどの処理も可能（必要に応じて）
        if (!usersRepository.existsByStudentId(studentId)) {
            Users user = new Users();
            user.setStudent_id(studentId);
            user.setUserName(userName);
            user.setLineId(lineId);
            usersRepository.save(user);
            return "createSuccess";
        } else {
        	
        	model.addAttribute("errorMessage", "正確な学籍番号を入力してください");
        	
        	return "createForm";
        }
    }
}