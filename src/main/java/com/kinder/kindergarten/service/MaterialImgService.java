package com.kinder.kindergarten.service;

import com.kinder.kindergarten.repository.MaterialImgRepository;
import com.kinder.kindergarten.entity.MaterialImgEntity;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class MaterialImgService {


    @Value("${materialImgLocation}")
    private String materialImgLocation;

    private final MaterialImgRepository materialImgRepository;
    private final FileService fileService;

    public void saveMaterialImg(MaterialImgEntity materialImgEntity, MultipartFile materialImgFile) throws Exception{
        String oriImgName = materialImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        //파일 업로드
        if(!StringUtils.isEmpty(oriImgName)){
            imgName = fileService.uploadFile(materialImgLocation, oriImgName,
                    materialImgFile.getBytes());
            // 사용자가 상품의 이미지를 등록했다면 저장할 경로와 파일의 이름, 파일의 바이트 배열을 파일 업로드 파라미터로 uploadFile 메서드 호출
            imgUrl = "/images/material/" + imgName;
            // 저장한 상품의 이미지 불러올 경로를 설정 WebMvcConfig 에서 설정함 d:\shop이므로 /images/material/를 붙여줌
        }

        //상품 이미지 정보 저장
        materialImgEntity.updateMaterialImg(oriImgName, imgName, imgUrl);
        materialImgRepository.save(materialImgEntity);
    }

    public void updateMaterialImg(Long id, MultipartFile materialImgFile) throws Exception{
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
