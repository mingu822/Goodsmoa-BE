package com.goodsmoa.goodsmoa_BE.cart.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goodsmoa.goodsmoa_BE.cart.dto.delivery.TrackingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrackingService {

    @Value("${sweettracker.api.key}")
    private String apiKey;

    // 외부 API 호출 후 가공된 JSON형태로 변환 시켜주기 위한 도구
    // 외부 API 호출
    private final RestTemplate restTemplate = new RestTemplate();
    // JSON <-> JAVA 로 변환 시켜주는 도구
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TrackingResponse trackDelivery(String companyCode, String invoiceNumber) throws Exception {
        String url = "https://info.sweettracker.co.kr/api/v1/trackingInfo"
                + "?t_key=" + apiKey
                + "&t_code=" + companyCode
                + "&t_invoice=" + invoiceNumber;

        // 호출 시 이런식으로 URL 보내줌
        System.out.println("스윗트래커 호출 URL: " + url);

        String response = restTemplate.getForObject(url, String.class);
        JsonNode root = objectMapper.readTree(response);

        TrackingResponse result = new TrackingResponse();
        result.setStatus(true);
        result.setReceiverName(root.path("receiverName").asText());
        result.setSenderName(root.path("senderName").asText());
        result.setItemName(root.path("itemName").asText());

        List<TrackingResponse.TrackingDetail> details = new ArrayList<>();
        for (JsonNode node : root.path("trackingDetails")) {
            TrackingResponse.TrackingDetail detail = new TrackingResponse.TrackingDetail();
            detail.setTime(node.path("time").asText());
            detail.setTime(node.path("timeString").asText());
            detail.setKind(node.path("kind").asText());
            detail.setWhere(node.path("where").asText());
            details.add(detail);
        }
        result.setTrackingDetails(details);

        return result;
    }
}