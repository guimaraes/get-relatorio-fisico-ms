package br.com.getnet.reportgatewayservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Value("${services.basic-report-url}")
    private String basicReportUrl;

    @Value("${services.full-report-url}")
    private String fullReportUrl;

    @Bean
    public String getBasicReportUrl() {
        System.out.println("Basic Report URL: " + basicReportUrl);
        return basicReportUrl;
    }

    @Bean
    public String getFullReportUrl() {
        System.out.println("Full Report URL: " + fullReportUrl);
        return fullReportUrl;
    }
}
