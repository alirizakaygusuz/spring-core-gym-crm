package com.alirizakaygusuz.gymcrm.workload_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class WorkloadServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkloadServiceApplication.class, args);
	}

}
