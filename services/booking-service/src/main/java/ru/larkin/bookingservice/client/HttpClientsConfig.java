package ru.larkin.bookingservice.client;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
@EnableConfigurationProperties(HotelManagementClientProperties.class)
public class HttpClientsConfig {

    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        // Базовые таймауты на уровне HTTP-клиента (дополнительно к retry-логике)
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();

        JdkClientHttpRequestFactory rf = new JdkClientHttpRequestFactory(httpClient);
        rf.setReadTimeout(Duration.ofSeconds(2));

        return builder
                .requestFactory(rf)
                .build();
    }
}
