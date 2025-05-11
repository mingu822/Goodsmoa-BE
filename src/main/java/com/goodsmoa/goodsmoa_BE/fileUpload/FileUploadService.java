package com.goodsmoa.goodsmoa_BE.fileUpload;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileUploadService {

    private final String baseUploadDir = "src/main/resources/static/trade/";

    public String uploadSingleImage(MultipartFile image, String subFolder) {
        if (image == null || image.isEmpty()) return null;

        try {
            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
            Path dirPath = Paths.get(baseUploadDir + subFolder);
            Path filePath = dirPath.resolve(fileName);

            Files.createDirectories(dirPath); // 폴더 없으면 생성
            Files.write(filePath, image.getBytes());

            return subFolder + "/" + fileName; // 상대 경로 리턴
        } catch (Exception e) {
            throw new RuntimeException("이미지 업로드 실패: " + e.getMessage());
        }
    }

    public List<String> uploadMultiImages(List<MultipartFile> images, String subFolder) {
        List<String> imageUrls = new ArrayList<>();
        if (images == null) return imageUrls;

        for (MultipartFile image : images) {
            String url = uploadSingleImage(image, subFolder);
            if (url != null) {
                imageUrls.add(url);
            }
        }

        return imageUrls;
    }
}
