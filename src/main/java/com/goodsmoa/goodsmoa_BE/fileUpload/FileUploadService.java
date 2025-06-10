package com.goodsmoa.goodsmoa_BE.fileUpload;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileUploadService {

    private final String baseUploadDir = "src/main/resources/static/";

    // 단일 이미지 업로드
    public String uploadSingleImage(MultipartFile image, String subFolder, Long id) {
        if (image == null || image.isEmpty())
            return null;

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
        if (images == null || images.isEmpty())
            return imageUrls;

        try {
            Path dirPath = Paths.get(baseUploadDir + subFolder);
            Files.createDirectories(dirPath);

            int index = 1;
            for (MultipartFile image : images) {
                if (image.isEmpty())
                    continue;
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
    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".png"; // 기본 확장자 지정
        }
        return filename.substring(filename.lastIndexOf(".")).toLowerCase(); // 소문자 통일
    }

    // 기존에 폴더에 존재했던 이미지 파일 삭제
    public void deleteImage(String relativePath) {
        try {
            Path imagePath = Paths.get(baseUploadDir, relativePath);
            Files.deleteIfExists(imagePath);
        } catch (IOException e) {
            throw new RuntimeException("기존 이미지 삭제 실패: " + e.getMessage());
        }
    }

    public String uploadImageWithCustomName(MultipartFile image, String subFolder, String fileNameWithoutExt) {
        try {
            Path dirPath = Paths.get(baseUploadDir + subFolder);
            Files.createDirectories(dirPath);

            String extension = getExtension(image.getOriginalFilename());
            String fileName = fileNameWithoutExt + extension;
            Path filePath = dirPath.resolve(fileName);

            Files.write(filePath, image.getBytes());

            return subFolder + "/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 실패: " + e.getMessage());
        }
    }
}
