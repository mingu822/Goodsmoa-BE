package com.goodsmoa.goodsmoa_BE.product.dto.Image;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Builder
@Getter
public class ProductImageUpdateRequest {

    // 새로운 썸네일 이미지
    private MultipartFile newThumbnailImage;

    // 사용 x
    // 새로운 상세설명 이미지 및 삭제할 아이디
    private List<MultipartFile> newContentImages;
    private List<String> deleteContentImageIds;

    // 새로운 상품이미지 및 삭제할 아이디
    private List<MultipartFile> newProductImages;
    private List<Long> deleteProductIds;

}
