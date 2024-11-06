package com.kinder.kindergarten.service.material;

import com.kinder.kindergarten.entity.material.MaterialImgEntity;
import com.kinder.kindergarten.repository.material.MaterialImgRepository;
import com.kinder.kindergarten.repository.material.MaterialRepository;
import com.kinder.kindergarten.service.FileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class MaterialImgService {


    @Value("${materialImgLocation}")
    private String materialImgLocation;

    private final MaterialImgRepository materialImgRepository;
    private final FileService fileService;
    private final MaterialRepository materialRepository;

    public void saveMaterialImg(MaterialImgEntity materialImgEntity, MultipartFile materialImgFile) throws Exception{

        String oriImgName = materialImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        log.info("파일 이미지 : oriImgName 생성 여부 확인  : " + oriImgName);


        //파일 업로드
        if(!StringUtils.isEmpty(oriImgName)){
            imgName = fileService.uploadFile(materialImgLocation, oriImgName,
                    materialImgFile.getBytes());
            // 사용자가 상품의 이미지를 등록했다면 저장할 경로와 파일의 이름, 파일의 바이트 배열을 파일 업로드 파라미터로 uploadFile 메서드 호출
            log.info("파일 이미지 : imgName 생성 여부 확인  : " + imgName);
            imgUrl = "/images/material/" + imgName;
            // 저장한 상품의 이미지 불러올 경로를 설정 WebMvcConfig 에서 설정함 d:\shop이므로 /images/material/를 붙여줌
            log.info("파일 이미지 : imgUrl 생성 여부 확인  : " + imgUrl);
        }

        //상품 이미지 정보 저장
        materialImgEntity.updateMaterialImg(oriImgName, imgName, imgUrl);
        materialImgRepository.save(materialImgEntity);
    }

    public void updateMaterialImg(String id, MultipartFile materialImgFile) throws Exception{
        if(!materialImgFile.isEmpty()){
            MaterialImgEntity savedMaterialImg = materialImgRepository.findById(id)
                    .orElseThrow(EntityNotFoundException::new);

            //기존 이미지 파일 삭제
            if(!StringUtils.isEmpty(savedMaterialImg.getMaterial_imgName())) {
                fileService.deleteFile(materialImgLocation+"/"+
                        savedMaterialImg.getMaterial_imgName());
            }

            String oriImgName = materialImgFile.getOriginalFilename(); // MultipartFile 기능 getOriginalFilename
            String imgName = fileService.uploadFile(materialImgLocation, oriImgName, materialImgFile.getBytes());
            String imgUrl = "/images/material/" + imgName;
            savedMaterialImg.updateMaterialImg(oriImgName, imgName, imgUrl);
        }
    }


}
