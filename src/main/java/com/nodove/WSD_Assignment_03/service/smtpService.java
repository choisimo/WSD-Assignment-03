package com.nodove.WSD_Assignment_03.service;

import com.nodove.WSD_Assignment_03.dto.users.emailRequest;
import com.nodove.WSD_Assignment_03.repository.usersRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class smtpService {

    private final usersRepository usersRepository;
    @Value("${server.email.sender}")
    private String sender;
    private final JavaMailSender javaMailSender;
    private final redisService redisService;

    private String UUIDGenerator() {
        return UUID.randomUUID().toString().substring(0, 8);
    }


    // 이메일 중복 확인
    public boolean checkEmailExists(emailRequest request) {
        if (redisService.checkEmailExists(request.getEmail()))
        {
            log.info("이미 존재하는 이메일: {}", request.getEmail());
            return true;
        }
        boolean isExists = this.usersRepository.findByEmail(request.getEmail()).isPresent();

        if (isExists) {
            log.info("이미 존재하는 이메일: {}", request.getEmail());
            redisService.saveEmail(request.getEmail());
            return true;
        }
        return false;
    }

    // 회원가입 인증 메일 전송
    @Transactional
    public String sendJoinMail(emailRequest request) {

        if (checkEmailExists(request)) return null; // 이메일 중복 확인
        String uuid = UUIDGenerator(); // 인증번호 생성
        String subject = "회원가입 인증 메일입니다.";
        String text = "<h1>회원가입 인증번호</h1>" +
                "<p>다음 인증번호를 입력해 주세요: <strong>" + uuid + "</strong></p>"; // HTML 형식

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            helper.setFrom(sender); // 발신자
            helper.setTo(request.getEmail()); // 수신자
            helper.setSubject(subject); // 메일 제목
            helper.setText(text, true); // HTML 형식 메시지

            javaMailSender.send(mimeMessage);
            log.info("메일 전송 성공: {}", request.getEmail());
        } catch (MessagingException e) {
            log.error("메일 전송 실패: {}", request.getEmail(), e);
            return "fail";
        }
        return uuid;
    }
}
