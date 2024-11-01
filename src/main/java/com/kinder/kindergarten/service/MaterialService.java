package com.kinder.kindergarten.service;


import com.kinder.kindergarten.DTO.MaterialFormDTO;
//import com.kinder.kindergarten.repository.MaterialImgRepository;


import com.kinder.kindergarten.DTO.MaterialSearchDTO;
import com.kinder.kindergarten.entity.MaterialEntity;
import com.kinder.kindergarten.entity.MaterialImgEntity;
import com.kinder.kindergarten.repository.MaterialImgRepository;
import com.kinder.kindergarten.repository.MaterialRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final MaterialImgService materialImgService;

    private final FileService fileService;
    private final MaterialImgRepository materialImgRepository;

    public Long saveMaterial(MaterialFormDTO materialFormDTO, List<MultipartFile> materialImgFileList) throws Exception{

        // 자재 등록
        MaterialEntity materialEntity = materialFormDTO.createMaterial();
        materialRepository.save(materialEntity);
        
        // 이미지 등록
        for (int i=0; i < materialImgFileList.size(); i++){
            MaterialImgEntity materialImgEntity = new MaterialImgEntity();
            materialImgEntity.setMaterialEntity(materialEntity);
            if (i == 0)
                materialImgEntity.setMaterial_repimgYn("Y");
            else
                materialImgEntity.setMaterial_repimgYn("N");
            materialImgService.saveMaterialImg(materialImgEntity, materialImgFileList.get(i));
        }

        return materialEntity.getId();
    }



    @Transactional(readOnly = true) // 상품을 읽어오는 트랜젝션을 읽기 전용으로 설정하면 성능이 개선됨.(더티체킹(변경감지) 수행않음)
    public MaterialFormDTO getMaterialDtl(Long id){
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

    public Long updateMaterial(MaterialFormDTO materialFormDTO, List<MultipartFile> materialImgFileList) throws Exception{
        //상품 수정
        MaterialEntity materialEntity = materialRepository.findById(materialFormDTO.getId())
                .orElseThrow(EntityNotFoundException::new);
        materialEntity.updateMaterial(materialFormDTO);
        List<Long> materialImgIds = materialFormDTO.getMaterialImgIds();

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

/*    @Transactional
    public void deleteMaterial(Long id) throws Exception {
        // 자재 이미지 먼저 삭제
        List<MaterialImgEntity> materialImgList = materialImgRepository.findByIdOrderByIdAsc(id);
        for(MaterialImgEntity materialImg : materialImgList) {
            fileService.deleteFile(materialImg.getMaterial_ImgUrl()); // 실제 이미지 파일 삭제
            materialImgRepository.delete(materialImg); // 이미지 엔티티 삭제
        }
        
        // 자재 삭제
        MaterialEntity material = materialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("자재를 찾을 수 없습니다."));
        materialRepository.delete(material);
    }*/

    @Transactional
    public void deleteMaterial(Long id) throws Exception {
        // 자재 이미지 먼저 삭제
        List<MaterialImgEntity> materialImgList = materialImgRepository.findByMaterialEntityId(id); // 메서드 수정 필요
        for(MaterialImgEntity materialImg : materialImgList) {
            fileService.deleteFile(materialImg.getMaterial_ImgUrl()); // 실제 이미지 파일 삭제
            materialImgRepository.delete(materialImg); // 이미지 엔티티 삭제
        }

        // 자재 삭제
        MaterialEntity material = materialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("자재를 찾을 수 없습니다."));
        materialRepository.delete(material);
    }

}
