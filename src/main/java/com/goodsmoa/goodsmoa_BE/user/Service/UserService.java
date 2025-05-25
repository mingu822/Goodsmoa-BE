package com.goodsmoa.goodsmoa_BE.user.Service;


import com.goodsmoa.goodsmoa_BE.security.provider.JwtProvider;
import com.goodsmoa.goodsmoa_BE.user.Converter.AddressConverter;
import com.goodsmoa.goodsmoa_BE.user.DTO.AddressRequestDto;
import com.goodsmoa.goodsmoa_BE.user.DTO.UserInfoUpdateRequestDto;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserAddressEntity;
import com.goodsmoa.goodsmoa_BE.user.Repository.UserAddressRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import com.goodsmoa.goodsmoa_BE.user.Repository.UserRepository;

import java.util.List;

@Service // 이 클래스가 서비스임을 Spring에게 알려주는 어노테이션
@RequiredArgsConstructor
public class UserService {


    private final UserRepository userRepository;
    private final UserAddressRepository addressRepository;
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;


    // ID로 사용자 조회
    public UserEntity getUserById(String id) {
        // findById는 Optional을 반환하므로, orElseThrow()로 존재하지 않을 경우 예외 처리
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 ID의 사용자를 찾을 수 없습니다."));
    }


    public void updateUser( UserEntity user, UserInfoUpdateRequestDto dto) {

        // 2. 유저 정보 업데이트
        user.updateUserInfo(
                dto.getName(),
                dto.getNickname(),
                dto.getEmail(),
                dto.getPhoneNumber(),
                dto.getContent(),
                dto.getImage()
        );

        userRepository.save(user); // 변경 감지용 (안 해도 자동 저장됨)

        // 3. 기존 배송지 삭제
        addressRepository.deleteAllByUser(user);

        // 4. 새 배송지 등록 (★ 컨버터 사용!)
        List<AddressRequestDto> addressList = dto.getAddresses();
        for (AddressRequestDto addressDto : addressList) {
            UserAddressEntity address = AddressConverter.toEntity(addressDto, user); // 👈 여기!
            addressRepository.save(address);
        }
    }


    //  유저의 refreshToken 삭제 (로그아웃 시 사용)
    public void removeRefreshToken(UserEntity user) {
        if (user != null) {
            String redisKey = "RT:" + user.getId();
            redisTemplate.delete(redisKey);

        }
    }


    public String reissueAccessTokenFromRefresh(String refreshToken) {
        try {
            // 토큰 파싱해서 유저 ID만 추출 (JwtProvider는 변경 못하니 여기서 파싱)
            Claims body = Jwts.parser()
                    .setSigningKey(jwtProvider.getShaKey())
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody();

            String userId = body.get("id").toString();
            String redisKey = "RT:" + userId;

            String encryptedRT = redisTemplate.opsForValue().get(redisKey);
            if (encryptedRT == null) {
                throw new RuntimeException("Redis에 리프레시 토큰 없음");
            }

            String decryptedRT = jwtProvider.decrypt(encryptedRT);
            if (!decryptedRT.equals(refreshToken) || !jwtProvider.validateToken(decryptedRT)) {
                throw new RuntimeException("리프레시 토큰 불일치 또는 만료됨");
            }

            UserEntity user = getUserById(userId);
            return jwtProvider.createAccessToken(user);

        } catch (Exception e) {

            throw new RuntimeException("엑세스 토큰 재발급 실패: " + e.getMessage());
        }
    }









}
