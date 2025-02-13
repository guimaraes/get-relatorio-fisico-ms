package br.com.getnet.reportgatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ReportGatewayServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(ReportGatewayServiceApplication.class, args);
	}
}
