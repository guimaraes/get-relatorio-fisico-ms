package br.com.getnet.fullreport;

import org.springframework.boot.SpringApplication;

public class TestFullReportServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(FullReportServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
