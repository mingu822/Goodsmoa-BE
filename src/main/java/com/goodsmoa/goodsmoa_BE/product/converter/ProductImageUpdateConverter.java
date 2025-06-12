package com.goodsmoa.goodsmoa_BE.product.converter;

import com.goodsmoa.goodsmoa_BE.product.dto.Image.ProductImageUpdateRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
public class ProductImageUpdateConverter {
    public ProductImageUpdateRequest toUpdate(
            MultipartFile newThumbnailImage,
            List<MultipartFile> newContentImages,
            List<MultipartFile> newProductImages,
            List<Long> deleteProductImageIds
    ){
        return ProductImageUpdateRequest.builder()
                .newThumbnailImage(newThumbnailImage)
                .newContentImages(newContentImages)
                .newProductImages(newProductImages)
                .deleteProductIds(deleteProductImageIds)
                .build();
    }
}
