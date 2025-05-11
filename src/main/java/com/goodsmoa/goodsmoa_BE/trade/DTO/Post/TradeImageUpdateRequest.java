package com.goodsmoa.goodsmoa_BE.trade.DTO.Post;

import lombok.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeImageUpdateRequest {

    private MultipartFile newThumbnailImage;

    private List<Long> deleteProductImageIds;
    private List<MultipartFile> newProductImages;

    private List<Long> deleteContentImageIds;
    private List<MultipartFile> newContentImages;
}
