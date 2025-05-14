package com.goodsmoa.goodsmoa_BE.product.converter;

import com.goodsmoa.goodsmoa_BE.product.dto.Image.ProductImageUpdateRequest;
import com.goodsmoa.goodsmoa_BE.trade.DTO.Post.TradeImageUpdateRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
public class ProductImageUpdateConverter {
    public ProductImageUpdateRequest toUpdate(
            MultipartFile newThumbnailImage,
            List<MultipartFile> newContentImages,
            List<MultipartFile> newProductImages,
            List<String> deleteContentImageIds,
            List<Long> deleteProductImageIds
    ){
        return ProductImageUpdateRequest.builder()
                .newThumbnailImage(newThumbnailImage)
                .newContentImages(newContentImages)
                .newProductImages(newProductImages)
                .deleteContentImageIds(deleteContentImageIds)
                .deleteProductImageIds(deleteProductImageIds)
                .build();
    }
}
