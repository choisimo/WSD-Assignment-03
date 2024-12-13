package com.nodove.WSD_Assignment_03;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;


@SpringBootApplication
public class WsdAssignment03Application {

	public static void main(String[] args) {
		SpringApplication.run(WsdAssignment03Application.class, args);
	}

	// @PostConstruct 설명
	// 초기화 메서드란 스프링 빈이 생성된 후 별도의 초기화 작업을 위해 실행하는 메서드
	@PostConstruct
	void set_time_zone() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}
}
