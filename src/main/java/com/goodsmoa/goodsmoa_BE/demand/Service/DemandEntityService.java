package com.goodsmoa.goodsmoa_BE.demand.Service;

import com.goodsmoa.goodsmoa_BE.demand.Db.DemandEntityRepository;
import com.goodsmoa.goodsmoa_BE.demand.Dto.DemandEntityRequest;
import com.goodsmoa.goodsmoa_BE.demand.Entity.DemandEntity;
import com.goodsmoa.goodsmoa_BE.demand.Converter.DemandConverter;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DemandEntityService {

    private final DemandEntityRepository demandEntityRepository;
    private final DemandConverter demandConverter;

    // 비공개,종료되지 않은 모든 글 가져오기
/*    @Transactional
    public List<DemandEntity> getDemandEntityList() {
        return demandEntityRepository.findAllByEndTimeAfterAndState(LocalDateTime.now(), 1);
    }*/

    // 생성 혹은 변경
    @Transactional
    public void createOrUpdateDemand(DemandEntityRequest request) {
        if(demandEntityRepository.existsById(request.getId())){
            DemandEntity demandEntity = findByIdWithThrow(request.getId());
            demandEntity.updateDemandEntity(
                    request.getTitle(),
                    request.getDescription(),
                    request.getStartTime(),
                    request.getEndTime(),
                    request.getImage(),
                    request.getHashtag()
            );
        }
        else{
            demandEntityRepository.save(demandConverter.toEntity(request));
        }
    }



    // 수요조사 글 조회
    public DemandEntity findByIdWithThrow(Long id){
        return demandEntityRepository.findDemandEntityById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 수요조사는 존재하지 않습니다."));
    }



}
