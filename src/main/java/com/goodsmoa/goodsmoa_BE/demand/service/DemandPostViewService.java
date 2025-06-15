package com.goodsmoa.goodsmoa_BE.demand.service;

import com.goodsmoa.goodsmoa_BE.demand.repository.DemandPostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class DemandPostViewService {
    private static final String VIEW_COUNT_KEY = "demandPost:view:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final DemandPostRepository demandPostRepository;

    public void increaseViewCount(Long demandId){
        String redisKey = VIEW_COUNT_KEY + demandId;
        redisTemplate.opsForValue().increment(redisKey);
    }

    @Scheduled(cron = "0 */1 * * * *")
    @Transactional
    public void syncViewCountToDatabase(){
        log.info("수요조사 조회수 동기화 시작합니다");
        Set<String> keys = scanKeys(VIEW_COUNT_KEY + "*");

        if(keys.isEmpty()){
            return;
        }
        for (String key : keys) {
            Long demandId = Long.parseLong(key.replace(VIEW_COUNT_KEY, ""));
            Object redisValue = redisTemplate.opsForValue().get(key);
            Long views = redisValue != null ? Long.parseLong(redisValue.toString()) : 0L;

            demandPostRepository.findById(demandId).ifPresent(demandPost -> {
                demandPost.setViews(views);
            });
            redisTemplate.delete(key);
        }
    }

    private Set<String> scanKeys(String pattern){
        return redisTemplate.execute((RedisCallback<Set<String>>) connection ->{
            Set<String> keys = new HashSet<>();
            ScanOptions options = ScanOptions.scanOptions().match(pattern).count(1000).build();
            try( var cursor = connection.scan(options)){
                while(cursor.hasNext()){
                    byte[] keyBytes = cursor.next();
                    keys.add(new String(keyBytes , StandardCharsets.UTF_8));
                }
            }catch (Exception e){
                log.error("Redis scan error: ",e);
            }
            return keys;
        } );
    }
}
