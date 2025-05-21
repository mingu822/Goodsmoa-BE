package com.goodsmoa.goodsmoa_BE.demand.converter;

import com.goodsmoa.goodsmoa_BE.demand.entity.DemandPostDocument;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandPostEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DemandPostSearchConverter {
    public DemandPostDocument toDocument(DemandPostEntity entity){
        return DemandPostDocument.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .hashtag(entity.getHashtag())
                .nickname(entity.getUser().getNickname())
                .categoryId(entity.getCategory().getId())
                .isSafePayment(true)
                .isState(true)
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .pulledAt(LocalDateTime.now())
                .build();
    }

}
