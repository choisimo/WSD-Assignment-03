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

	@PostConstruct
	void set_time_zone() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}
}
