//package com.goodsmoa.goodsmoa_BE.demand.scheduler;
//
//import com.goodsmoa.goodsmoa_BE.demand.service.DemandLikeService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//@Component
//@RequiredArgsConstructor
//public class DemandLikeSyncScheduler {
//    private final DemandLikeService demandLikeService;
//
//    @Scheduled(fixedRate = 60000)
//    @Transactional
//    public void syncLikesToDB() {
//        demandLikeService.syncLikesToDB();
//    }
//}
