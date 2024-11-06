package com.kinder.kindergarten.service.money;

import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;

import com.kinder.kindergarten.DTO.money.MoneyFileDTO;
import com.kinder.kindergarten.DTO.money.MoneyFormDTO;
import com.kinder.kindergarten.DTO.money.MoneySearchDTO;
import com.kinder.kindergarten.entity.money.MoneyEntity;
import com.kinder.kindergarten.entity.money.MoneyFileEntity;
import com.kinder.kindergarten.repository.money.MoneyFileRepository;
import com.kinder.kindergarten.repository.money.MoneyRepository;
import com.kinder.kindergarten.service.FileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class MoneyService {

    private final MoneyRepository moneyRepository;

    private final MoneyFileRepository moneyFileRepository;

    private final FileService fileService;

    private final ModelMapper modelMapper;

    //파일 업로드 경로
    @Value("${uploadPathMoney1}")
    private String uploadPath;

    // 이미지 파일인지 확인하는 메소드
    private boolean isImageFile(String extension) {
        return Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".bmp")
                .contains(extension.toLowerCase());
    }

    //첨부파일이 있는경우 불러오기(다운로드용)
    public MoneyFileDTO getMoneyFile(String fileId) {
        MoneyFileEntity fileEntity = moneyFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));

        MoneyFileDTO fileDTO = new MoneyFileDTO();
        String uploadFileName = fileEntity.getOriginalName();
        String encodedUploadFile = UriUtils.encode(uploadFileName, StandardCharsets.UTF_8);

        fileDTO.setFileId(fileEntity.getFileId());
        //fileDTO.setOrignalName(fileEntity.getOriginalName());
        fileDTO.setOrignalName(encodedUploadFile);
        fileDTO.setFilePath(fileEntity.getFilePath());

        return fileDTO;
    }

    // 글작성 Create
    public String saveMoney(MoneyFormDTO moneyFormDTO){

        // ULID 생성
        Ulid ulid = UlidCreator.getUlid();
        String id = ulid.toString();
        moneyFormDTO.setMoneyId(id);
        MoneyEntity moneyEntity = moneyFormDTO.createMoney();
        moneyRepository.save(moneyEntity);

        return moneyEntity.getMoneyId();
    }

    // 글작성 Create + 첨부파일 처리
    public void saveMoneyAndFile(MoneyFormDTO moneyFormDTO, List<MultipartFile> moneyFileList) throws Exception {

        Ulid ulid = UlidCreator.getUlid();
        String id = ulid.toString();
        moneyFormDTO.setMoneyId(id);
        MoneyEntity money = moneyFormDTO.createMoney();
        moneyRepository.save(money); // 게시물 정보 저장

        if (moneyFileList != null && !moneyFileList.get(0).isEmpty()){
            boolean isFirstImage = true;
            String mainFileName = null;

            for (MultipartFile file : moneyFileList){
                MoneyFileEntity moneyFile = new MoneyFileEntity();

                String originalFilename =file.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

                // FileService를 사용하여 파일 저장
                String savedFileName = fileService.uploadFile(uploadPath, originalFilename, file.getBytes());
                String filePath = fileService.getFullPathMoney(savedFileName);

                // 이미지 파일인 경우 첫 번째 파일을 mainFile로 지정
                if (isFirstImage && isImageFile(extension)) {
                    mainFileName = savedFileName;
                    isFirstImage = false;
                }
                // moneyFileEntity 설정
                moneyFile.setOriginalName(originalFilename);
                moneyFile.setModifiedName(savedFileName);
                moneyFile.setFilePath(filePath);
                moneyFile.setMainFile(mainFileName != null ? mainFileName : ""); // 메인 파일 이름 설정

                // 새로운 파일 ID 생성
                Ulid fileUlid = UlidCreator.getUlid();
                moneyFile.setFileId(fileUlid.toString());

                // moneyEntity 설정
                moneyFile.setMoneyEntity(money);
                
                // 파일 정보 저장
                moneyFileRepository.save(moneyFile);
            }
        }

    }
    
    // 회계 Money 페이징 처리
    @Transactional(readOnly = true)
    public Page<MoneyEntity> getMoneyPage(MoneySearchDTO moneySearchDTO, Pageable pageable){
        return moneyRepository.getMoneyPage(moneySearchDTO, pageable);
    }


    @Transactional(readOnly = true)
    public MoneyFormDTO getMoneyDtl(String moneyId){
        Optional<MoneyFormDTO> optionalMoneyFormDTO = moneyRepository.findById(moneyId).map(moneyEntity -> modelMapper.map(moneyEntity,MoneyFormDTO.class));
        MoneyFormDTO moneyFormDTO = optionalMoneyFormDTO.get();

        List<MoneyFileEntity> fileEntities = moneyFileRepository.findByMoneyEntity_moneyId(moneyId);
        log.info("파일정보1 : " + fileEntities);
        if (!fileEntities.isEmpty()) {
            List<MoneyFileDTO> fileDTOs = fileEntities.stream()
                    .map(fileEntity -> {
                        MoneyFileDTO fileDTO = new MoneyFileDTO();
                        fileDTO.setFileId(fileEntity.getFileId());
                        fileDTO.setOrignalName(fileEntity.getOriginalName());
                        fileDTO.setModifiedName(fileEntity.getModifiedName());
                        fileDTO.setFilePath(fileEntity.getFilePath());
                        fileDTO.setMainFile(fileEntity.getMainFile());
                        fileDTO.setBoardId(moneyId);
                        return fileDTO;
                    })
                    .collect(Collectors.toList());

            moneyFormDTO.setMoneyFileList(fileDTOs);
            log.info("파일정보 : " + fileDTOs);
        }

        return moneyFormDTO;
    }


    // update 회계 수정 관련 (입력 정보 반영)
    public String updateMoney(MoneyFormDTO moneyFormDTO){
        //상품 수정
        MoneyEntity moneyEntity = moneyRepository.findById(moneyFormDTO.getMoneyId())
                .orElseThrow(EntityNotFoundException::new);
        moneyEntity.updateMoney(moneyFormDTO);

        return moneyEntity.getMoneyId();
    }

    // Delete 회계 삭제
    @Transactional
    public void deleteMoney(String moneyId) throws Exception{
        // 자재 이미지 먼저 삭제
        List<MoneyFileEntity> moneyFileEntityList = moneyFileRepository.findByMoneyEntity_moneyId(moneyId); // 메서드 수정 필요
        for(MoneyFileEntity moneyFile : moneyFileEntityList) {
            fileService.deleteFile(moneyFile.getFilePath()); // 실제 이미지 파일 삭제
            moneyFileRepository.delete(moneyFile); // 이미지 엔티티 삭제
        }

        MoneyEntity moneyEntity = moneyRepository.findById(moneyId)
                .orElseThrow(() -> new EntityNotFoundException("자재를 찾을 수 없습니다."));
        moneyRepository.delete(moneyEntity);
    }


}
