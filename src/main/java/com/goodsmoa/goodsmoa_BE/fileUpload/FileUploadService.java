package com.goodsmoa.goodsmoa_BE.fileUpload;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileUploadService {

    private final String baseUploadDir = "src/main/resources/static/";

    // 단일 이미지 업로드
    public String uploadSingleImage(MultipartFile image, String subFolder, Long id) {
        if (image == null || image.isEmpty()) return null;

        try {
            String extension = getExtension(image.getOriginalFilename()); // 확장자 추출
            String fileName = id + "_" + "1" + extension; // tradeId
            Path dirPath = Paths.get(baseUploadDir + subFolder);
            Path filePath = dirPath.resolve(fileName);

            Files.createDirectories(dirPath); // 디렉터리 없으면 생성
            Files.write(filePath, image.getBytes());

            return subFolder + "/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException("이미지 업로드 실패: " + e.getMessage());
        }
    }

    // 다중 이미지 업로드
    public List<String> uploadMultiImages(List<MultipartFile> images, String subFolder, Long id) {
        List<String> imageUrls = new ArrayList<>();
        if (images == null || images.isEmpty()) return imageUrls;

        try {
            Path dirPath = Paths.get(baseUploadDir + subFolder);
            Files.createDirectories(dirPath);

            int index = 1;
            for (MultipartFile image : images) {
                if (image.isEmpty()) continue;
                String extension = getExtension(image.getOriginalFilename());
                String fileName = id + "_" + index++ + extension; // tradeId와 순번을 조합
                Path filePath = dirPath.resolve(fileName);

                Files.write(filePath, image.getBytes());
                imageUrls.add(subFolder + "/" + fileName);
            }
        } catch (Exception e) {
            throw new RuntimeException("이미지 업로드 실패: " + e.getMessage());
        }

        return imageUrls;
    }

    // 파일 확장자 추출
    private String getExtension(String originalFilename) {
        int dotIndex = originalFilename.lastIndexOf(".");
        return dotIndex != -1 ? originalFilename.substring(dotIndex) : "";
    }
}
