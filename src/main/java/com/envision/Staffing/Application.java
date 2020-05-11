package com.envision.Staffing;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
	public static void main(String[] args) {
		Logger log = Logger.getLogger(Application.class);
		SpringApplication.run(Application.class, args);
		log.info("=============================");
		log.info("Started OSAT Application ");
		log.info("=============================");
	}
}