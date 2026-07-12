package com.airlinebooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AirlineBookingBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(AirlineBookingBackendApplication.class, args);
	}

}
