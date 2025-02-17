package br.com.getnet.fullreport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "br.com.getnet.fullreport.service.client")
@SpringBootApplication
public class FullReportServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FullReportServiceApplication.class, args);
	}

}
