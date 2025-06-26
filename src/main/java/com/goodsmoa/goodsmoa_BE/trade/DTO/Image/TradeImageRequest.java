package com.goodsmoa.goodsmoa_BE.trade.DTO.Image;



import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeImageRequest {

//    private long postId;

//    private List<String> imagePath;

    private MultipartFile thumbnailImage;

    private List<MultipartFile> contentImages;

    private List<MultipartFile> productImages;
}

