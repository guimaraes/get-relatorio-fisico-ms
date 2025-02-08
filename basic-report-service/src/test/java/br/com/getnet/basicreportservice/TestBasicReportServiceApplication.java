package br.com.getnet.basicreportservice;

import org.springframework.boot.SpringApplication;

public class TestBasicReportServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(BasicReportServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
