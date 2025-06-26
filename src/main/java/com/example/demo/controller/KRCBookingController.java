package com.example.demo.controller;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.demo.repository.UsersRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class KRCBookingController {
	
	@Autowired
	private UsersRepository usersRepository;


    @Value("${line.bot.channel-secret}")
    private String channelSecret;

    @Value("${line.bot.channel-token}")
    private String channelToken;

    private static final String LINE_API_URL = "https://api.line.me/v2/bot/message/reply";

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
                                                @RequestHeader("X-Line-Signature") String signature
    ) {
        if (verifySignature(payload, signature)) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(payload);
                String replyToken = jsonNode.path("events").get(0).path("replyToken").asText();
                String messageText = jsonNode.path("events").get(0).path("message").path("text").asText();

                // LINEユーザーIDを取得
                String userId = jsonNode.get("events").get(0).get("source").get("userId").asText();
                //System.out.println("userId: " + userId);

                switch (messageText) {
                    case "カラオケ予約" -> sendKaraokeOptions(replyToken, userId);
                    case "面接ブース予約" -> sendInterviewBoothOptions(replyToken, userId);
                    case "登録" -> sendsiginin(replyToken, userId);
                    case "予約履歴" -> sendReservationHistory(replyToken, userId);  // ← 追加
                    case "使い方" -> sendExplanationOptions(replyToken,userId);
                }

                return ResponseEntity.ok("OK");
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing webhook");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
        }
    }

    private boolean verifySignature(String payload, String signature) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(channelSecret.getBytes(), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String encodedHash = Base64.getEncoder().encodeToString(hash);
            return encodedHash.equals(signature);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void sendReplyMessage(String replyToken, String message) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + channelToken);

        String body = String.format(
                "{\"replyToken\":\"%s\",\"messages\":[{\"type\":\"text\",\"text\":\"%s\"}]}",
                replyToken, message);

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        restTemplate.postForObject(LINE_API_URL, request, String.class);
    }

    private void sendsiginin(String replyToken, String userId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + channelToken);

        String urlWithParam = "https://newly-uncommon-quetzal.ngrok-free.app/createForm?lineId=" + userId;

        String body = String.format("""
                {
                  "replyToken": "%s",
                  "messages": [
                    {
                      "type": "template",
                      "altText": "ユーザー登録",
                      "template": {
                        "type": "buttons",
                        "text": "↓登録用URL",
                        "actions": [
                          {
                            "type": "uri",
                            "label": "登録する",
                            "uri": "%s"
                          }
                        ]
                      }
                    }
                  ]
                }
                """, replyToken, urlWithParam);

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        restTemplate.postForObject(LINE_API_URL, request, String.class);
    }

    private void sendKaraokeOptions(String replyToken, String userId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + channelToken);
        String urlWithParam = "https://newly-uncommon-quetzal.ngrok-free.app/karaokeBooking?lineId=" + userId;

        String body = String.format("""
                {
                  "replyToken": "%s",
                  "messages": [
                    {
                      "type": "template",
                      "altText": "カラオケの予約メニュー",
                      "template": {
                        "type": "buttons",
                        "text": "カラオケのご利用ですか？",
                        "actions": [
                          {
                            "type": "uri",
                            "label": "予約する",
                            "uri": "%s"
                          }
                        ]
                      }
                    }
                  ]
                }
                """, replyToken, urlWithParam);

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        restTemplate.postForObject(LINE_API_URL, request, String.class);
    }

    private void sendInterviewBoothOptions(String replyToken, String userId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + channelToken);
        
        String urlWithParam = "https://newly-uncommon-quetzal.ngrok-free.app/calender?lineId=" + userId;

        String body = String.format("""
                {
                  "replyToken": "%s",
                  "messages": [
                    {
                      "type": "template",
                      "altText": "面接用ブースの予約メニュー",
                      "template": {
                        "type": "buttons",
                        "text": "面接用ブースのご利用ですか？",
                        "actions": [
                          {
                            "type": "uri",
                            "label": "予約する",
                            "uri": "%s"
                          }
                        ]
                      }
                    }
                  ]
                }
                """, replyToken, urlWithParam);

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        restTemplate.postForObject(LINE_API_URL, request, String.class);
    }

    private void sendReservationHistory(String replyToken, String userId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + channelToken);

        String urlWithParam = "https://newly-uncommon-quetzal.ngrok-free.app/reservationhistory?lineId=" + userId;

        String body = String.format("""
                {
                  "replyToken": "%s",
                  "messages": [
                    {
                      "type": "template",
                      "altText": "予約履歴",
                      "template": {
                        "type": "buttons",
                        "text": "↓予約履歴を見る",
                        "actions": [
                          {
                            "type": "uri",
                            "label": "履歴を見る",
                            "uri": "%s"
				          }
                        ]
                      }
                    }
                  ]
                }
                """, replyToken, urlWithParam);

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        restTemplate.postForObject(LINE_API_URL, request, String.class);
    }
    private void sendExplanationOptions(String replyToken, String userId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + channelToken);

        String urlExplanation = "https://newly-uncommon-quetzal.ngrok-free.app/explanation?lineId=" + userId;
        String urlRegister = "https://newly-uncommon-quetzal.ngrok-free.app/createForm?lineId=" + userId;

        // LINE IDが既に存在するか確認（登録済みならtrue）
        boolean userExists = usersRepository.findByLineId(userId) != null;

        String actions;
        if (userExists) {
            // 登録済み → ユーザー登録ボタンは非表示
            actions = String.format("""
                {
                  "type": "uri",
                  "label": "使用方法説明",
                  "uri": "%s"
                }
            """, urlExplanation);
        } else {
            // 未登録 → 両方表示
            actions = String.format("""
                {
                  "type": "uri",
                  "label": "使用方法説明",
                  "uri": "%s"
                },
                {
                  "type": "uri",
                  "label": "ユーザー登録",
                  "uri": "%s"
                }
            """, urlExplanation, urlRegister);
        }

        String body = String.format("""
            {
              "replyToken": "%s",
              "messages": [
                {
                  "type": "template",
                  "altText": "使い方説明",
                  "template": {
                    "type": "buttons",
                    "text": "使い方はこちら",
                    "actions": [
                      %s
                    ]
                  }
                }
              ]
            }
        """, replyToken, actions);

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        restTemplate.postForObject(LINE_API_URL, request, String.class);
    }
}
