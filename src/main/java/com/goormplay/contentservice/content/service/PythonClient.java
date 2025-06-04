package com.goormplay.contentservice.content.service;

import com.goormplay.contentservice.content.dto.RecommendationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PythonClient {
    private final RestTemplate restTemplate;

    // 예: http://python-service:5000/recommend
    private final String pythonApiUrl = "python.api.url";

    public PythonClient(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public RecommendationResponse fetchRecommendation(String memberId) {
        Map<String, String> request = Map.of("memberId", memberId);

        ResponseEntity<RecommendationResponse> response = restTemplate.exchange(
                pythonApiUrl,
                HttpMethod.POST,
                new HttpEntity<>(request),
                RecommendationResponse.class
        );

        return response.getBody();
    }
}
