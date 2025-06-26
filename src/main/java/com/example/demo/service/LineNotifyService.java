package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LineNotifyService {

    @Value("${line.bot.channel-token}")
    private String channelToken;

    private static final String PUSH_API_URL = "https://api.line.me/v2/bot/message/push";

    public void pushMessage(String userId, String message) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + channelToken);

        String body = String.format("""
            {
              "to": "%s",
              "messages": [
                {
                  "type": "text",
                  "text": "%s"
                }
              ]
            }
            """, userId, message);

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        restTemplate.postForObject(PUSH_API_URL, request, String.class);
    }
}
