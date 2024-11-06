package com.kinder.kindergarten.service.material;


import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;
//import com.kinder.kindergarten.repository.Material.MaterialImgRepository;

import com.kinder.kindergarten.DTO.material.MaterialFormDTO;
import com.kinder.kindergarten.DTO.material.MaterialImgDTO;
import com.kinder.kindergarten.DTO.material.MaterialSearchDTO;

import com.kinder.kindergarten.entity.material.MaterialEntity;
import com.kinder.kindergarten.entity.material.MaterialImgEntity;

import com.kinder.kindergarten.repository.material.MaterialImgRepository;
import com.kinder.kindergarten.repository.material.MaterialRepository;
import com.kinder.kindergarten.service.FileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final MaterialImgService materialImgService;

    private final FileService fileService;
    private final MaterialImgRepository materialImgRepository;



    public String saveMaterial(MaterialFormDTO materialFormDTO, List<MultipartFile> materialImgFileList) throws Exception{
        // MaterialEntity materialEntity = materialFormDTO.createMaterial();


        // 중복되지 않는 ULID 생성
        Ulid ulid = UlidCreator.getUlid();
        String id = ulid.toString();
        materialFormDTO.setId(id); // UUID대신 사용할 ULID
        MaterialEntity materialEntity = materialFormDTO.createMaterial();
        log.info("ULID 생성 여부 확인  : " + materialEntity.getId());
        log.info("이름 생성 여부 확인  : " + materialEntity.getMaterial_name());
        log.info("카테고리 생성 여부 확인  : " + materialEntity.getMaterial_category());
        materialRepository.save(materialEntity);


        for (int i = 0; i < materialImgFileList.size(); i++) {
            MaterialImgEntity materialImgEntity = new MaterialImgEntity();
            log.info("MaterialImgEntity materialImgEntity = new MaterialImgEntity(); 확인 완료 : " + materialImgEntity);

            materialImgEntity.setMaterialEntity(materialEntity);
            Ulid fileUlid = UlidCreator.getUlid();
            materialImgEntity.setId(fileUlid.toString());


            log.info("materialImgEntity.setMaterialEntity(materialEntity); 완료 : " + materialImgEntity);

            if (i == 0)
                materialImgEntity.setMaterial_repimgYn("Y");
            else
                materialImgEntity.setMaterial_repimgYn("N");
            log.info("materialImgEntity.setMaterial_repimgYn; 확인 : " + materialImgEntity.getMaterial_repimgYn());


            materialImgService.saveMaterialImg(materialImgEntity, materialImgFileList.get(i));
            log.info("materialImgEntity.getId() 출력 확인 완료 : " + materialImgEntity.getId());

        }
        log.info("materialEntity.getId() 출력 확인 중 : " + materialEntity.getId());

        return materialEntity.getId();
    }

    @Transactional(readOnly = true) // 자재를 읽어오는 트랜젝션을 읽기 전용으로 설정하면 성능이 개선됨.(더티체킹(변경감지) 수행않음)
    public MaterialFormDTO getMaterialDtl(String id){
        List<MaterialImgEntity> materialImgList = materialImgRepository.findByIdOrderByIdAsc(id);
        List<MaterialImgDTO> boardFileImgDtoList = new ArrayList<>();
        for (MaterialImgEntity materialImgEntity : materialImgList) {
            MaterialImgDTO materialImgDTO = MaterialImgDTO.of(materialImgEntity);
            boardFileImgDtoList.add(materialImgDTO);
        }

        MaterialEntity materialEntity = (MaterialEntity) materialRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        MaterialFormDTO materialFormDTO = MaterialFormDTO.of(materialEntity);
        materialFormDTO.setMaterialImgDTOList(boardFileImgDtoList);
        return materialFormDTO;
    }

    public String updateMaterial(MaterialFormDTO materialFormDTO, List<MultipartFile> materialImgFileList) throws Exception{
        //상품 수정
        MaterialEntity materialEntity = materialRepository.findById(materialFormDTO.getId())
                .orElseThrow(EntityNotFoundException::new);
        materialEntity.updateMaterial(materialFormDTO);
        List<String> materialImgIds = materialFormDTO.getMaterialImgIds();

        //이미지 등록
        for(int i=0;i<materialImgFileList.size();i++){
            materialImgService.updateMaterialImg(materialImgIds.get(i),
                    materialImgFileList.get(i));
        }

        return materialEntity.getId();
    }

    @Transactional(readOnly = true)
    public Page<MaterialEntity> getMaterialPage(MaterialSearchDTO materialSearchDTO, Pageable pageable){
        return materialRepository.getMaterialPage(materialSearchDTO, pageable);
    } // 페이지 처리되는 아이템 처리용

    @Transactional
    public void deleteMaterial(String id) throws Exception {
        // 자재 이미지 먼저 삭제
        List<MaterialImgEntity> materialImgList = materialImgRepository.findByMaterialEntityId(id); // 메서드 수정 필요
        for(MaterialImgEntity materialImg : materialImgList) {
            fileService.deleteFile(materialImg.getMaterialImgUrl()); // 실제 이미지 파일 삭제
            materialImgRepository.delete(materialImg); // 이미지 엔티티 삭제
        }

        // 자재 삭제
        MaterialEntity material = materialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("자재를 찾을 수 없습니다."));
        materialRepository.delete(material);
    }

}
