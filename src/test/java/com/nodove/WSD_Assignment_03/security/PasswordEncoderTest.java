package com.nodove.WSD_Assignment_03.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordEncoderTest {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testPasswordEncryption() {
        //List<String> rawPasswords;
        String rawPasswords;
        try {
            // JSON 파일 읽기
            //String jsonContent = Files.readString(Path.of("security.json"));
            // JSON 파싱하여 비밀번호 리스트 추출
            //JsonNode root = objectMapper.readTree(jsonContent);
            //rawPasswords = objectMapper.convertValue(root.get("passwords"), List.class);
            rawPasswords = "ogmg verf fkfh cnfq";
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
/*
        // 각 비밀번호 암호화 및 검증
        for (String rawPassword : rawPasswords) {
            String encodedPassword = passwordEncoder.encode(rawPassword);
            System.out.println("Raw Password: " + rawPassword);
            System.out.println("Encoded Password: " + encodedPassword);

            // 비밀번호 검증
            assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
        }*/

        String encodedPassword = passwordEncoder.encode(rawPasswords);
        System.out.println("Raw Password: " + rawPasswords);
        System.out.println("Encoded Password: " + encodedPassword);
        assertTrue(passwordEncoder.matches(rawPasswords, encodedPassword));
    }

}
