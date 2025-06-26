package com.goodsmoa.goodsmoa_BE.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Uploader {


    private final S3Client s3Client;

    //  S3 버킷 이름을 application.properties 에서 가져옴
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;


    // S3에 이미지 업로드하는 메서드

    public String upload(MultipartFile file) throws IOException {

        //파일명 구성: UUID + 원래 파일명 (충돌 방지)
        String extension = getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + "." + extension;

        // 저장 폴더 경로 + 파일명 조합 (예: reflections/2025-05-30/xxxx.jpg)
        String folder = "reflections/" + LocalDate.now(); // 날짜별 폴더
        String key = folder + "/" + filename;

        // 업로드 요청 객체 생성
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucket)                      // 버킷 이름
                .key(key)                            // 파일 경로+이름
                .contentType(file.getContentType())  // ex) image/jpeg
                .build();

        //  S3에 실제 업로드
        s3Client.putObject(
                putRequest,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
        );

        //  업로드된 파일의 URL 생성해서 반환
        return s3Client.utilities()
                .getUrl(GetUrlRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build())
                .toString(); // → https://booknuri.s3.ap-northeast-2.amazonaws.com/reflections/2025-05-30/abc.jpg
    }


    // 확장자 추출 메서드
    private String getExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new IllegalArgumentException("파일 이름에 확장자가 없습니다.");
        }
        return originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
    }

    // S3에서 이미지 삭제하는 메서드
    public void delete(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            // 삭제할 파일 URL이 없으면 아무것도 하지 않음
            return;
        }
        try {
            String key = getKeyFromUrl(fileUrl);
            s3Client.deleteObject(builder -> builder.bucket(bucket).key(key));
        } catch (Exception e) {
            // 로깅 추가 권장
            // e.printStackTrace();
            // 삭제 실패 시에도 다른 로직은 계속 진행되도록 여기서 예외를 던지지 않을 수 있음
            // 또는 특정 비즈니스 요구사항에 따라 예외를 던져야 할 수도 있음
        }
    }

    // 전체 URL에서 S3 객체 키를 추출하는 헬퍼 메서드
    private String getKeyFromUrl(String fileUrl) {
        // S3 URL 형식: https://<bucket-name>.s3.<region>.amazonaws.com/<key>
        // URL에서 "amazonaws.com/" 다음 부분을 모두 key로 간주
        String prefix = "amazonaws.com/";
        int startIndex = fileUrl.indexOf(prefix);
        if (startIndex == -1) {
            throw new IllegalArgumentException("올바른 형식의 S3 URL이 아닙니다: " + fileUrl);
        }
        return fileUrl.substring(startIndex + prefix.length());
    }
}
