package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.repository.ClosedDayRepository;

@Service
public class ClosedDayService {

    @Autowired
    private ClosedDayRepository closedDayRepository;
    
    

}
