package br.com.getnet.reportgatewayservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "services")
public class ServiceProperties {
    private String basicReportUrl;
    private String fullReportUrl;
}