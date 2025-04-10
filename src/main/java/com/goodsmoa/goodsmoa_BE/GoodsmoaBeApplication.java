package com.goodsmoa.goodsmoa_BE;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GoodsmoaBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(GoodsmoaBeApplication.class, args);
	}

}
