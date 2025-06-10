package com.goodsmoa.goodsmoa_BE.cart.dto.delivery;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackingResponse {
    private boolean status;
    private String receiverName;
    private String senderName;
    private String itemName;
    private List<TrackingDetail> trackingDetails;

    @Getter
    @Setter
    public static class TrackingDetail {
        private String time;
        private String timeString;
        private String kind;
        private String where;
//        for (
//        JsonNode node : root.path("trackingDetails")) {
//            TrackingResponse.TrackingDetail detail = new TrackingResponse.TrackingDetail();
//            detail.setTime(node.path("timeString").asText());  // 또는 node.path("time").asText()
//            detail.setStatus(node.path("kind").asText());
//            detail.setLocation(node.path("where").asText());
//            details.add(detail);
    }
}