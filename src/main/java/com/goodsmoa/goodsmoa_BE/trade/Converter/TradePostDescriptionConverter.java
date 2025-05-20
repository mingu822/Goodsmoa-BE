package com.goodsmoa.goodsmoa_BE.trade.Converter;


import com.goodsmoa.goodsmoa_BE.trade.DTO.Post.DescriptionDTO;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostDescription;
import org.springframework.stereotype.Component;

@Component
public class TradePostDescriptionConverter {

    public DescriptionDTO toDto(TradePostDescription entity) {
        return DescriptionDTO.builder()
                .type(entity.getContentType())
                .value(entity.getValue())
                .sequence(entity.getSequence())
                .textStyle(entity.getTestStyle())
                .fontSize(entity.getFontSize())
                .textAlignment(entity.getTextAlignment())
                .build();
    }

    public TradePostDescription toEntity(DescriptionDTO dto) {
        return TradePostDescription.builder()
                .contentType(dto.getType())
                .value(dto.getValue())
                .sequence(dto.getSequence())
                .testStyle(dto.getTextStyle() != null ? dto.getTextStyle() : TradePostDescription.TextStyle.NORMAL)
                .fontSize(dto.getFontSize())
                .textAlignment(dto.getTextAlignment())
                .build();
    }
}
