package com.goodsmoa.goodsmoa_BE.trade.Converter;

import com.goodsmoa.goodsmoa_BE.trade.DTO.Post.TradeImageUpdateRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
public class TradeImageUpdateConverter {
    public static TradeImageUpdateRequest toUpdate(
            MultipartFile newThumbnailImage,
            List<MultipartFile> newContentImages,
            List<MultipartFile> newProductImages,
            List<Long> deleteProductImageIds
    ){
        return TradeImageUpdateRequest.builder()
                .newThumbnailImage(newThumbnailImage)
                .newContentImages(newContentImages)
                .newProductImages(newProductImages)
                .deleteProductImageIds(deleteProductImageIds)
                .build();
    }
}
