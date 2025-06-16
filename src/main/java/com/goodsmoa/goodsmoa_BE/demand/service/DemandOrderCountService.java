package com.goodsmoa.goodsmoa_BE.demand.service;

import com.goodsmoa.goodsmoa_BE.demand.repository.DemandPostProductRepository;
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
public class DemandOrderCountService {
    private static final String DEMAND_ORDER_KEY = "demandPostProduct:orderCount";

    private final RedisTemplate<String, Object> redisTemplate;
    private final DemandPostProductRepository demandPostProductRepository;

    public void increaseOrderCount(Long demandProductId, int quantity){
        String redisKey = DEMAND_ORDER_KEY + demandProductId;
        redisTemplate.opsForValue().increment(redisKey, quantity);
    }

    public void decreaseOrderCount(Long demandProductId, int quantity){
        String redisKey = DEMAND_ORDER_KEY + demandProductId;
        redisTemplate.opsForValue().decrement(redisKey, quantity);
    }

    @Scheduled(cron = "0 */1 * * * *")
    @Transactional
    public void rate() {
        log.info("수요조사 달성율 동기화 시작합니다");
        Set<String> keys = scanKeys(DEMAND_ORDER_KEY+"*");

        if(keys.isEmpty()) return;
        for (String key : keys) {
            Long demandProductId = Long.parseLong(key.replace(DEMAND_ORDER_KEY, ""));
            Object redisValue = redisTemplate.opsForValue().get(key);
            int orderCount = redisValue != null ? Integer.parseInt(redisValue.toString()) : 0;

            demandPostProductRepository.findById(demandProductId).ifPresent(demandPostProduct -> {
                int currentCount = demandPostProduct.getOrderCount();
                int diff = currentCount+orderCount;
                demandPostProduct.setOrderCount(diff);
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