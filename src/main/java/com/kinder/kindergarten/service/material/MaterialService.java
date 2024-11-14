package com.kinder.kindergarten.service.material;



import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;
import com.kinder.kindergarten.DTO.material.*;
import com.kinder.kindergarten.constant.material.MaterialOrderStatus;
import com.kinder.kindergarten.entity.material.MaterialEntity;
import com.kinder.kindergarten.entity.material.MaterialFileEntity;
import com.kinder.kindergarten.entity.material.MaterialOrderEntity;
import com.kinder.kindergarten.entity.material.MaterialOrderHistory;
import com.kinder.kindergarten.repository.material.MaterialFileRepository;
import com.kinder.kindergarten.repository.material.MaterialOrderHistoryRepository;
import com.kinder.kindergarten.repository.material.MaterialOrderRepository;
import com.kinder.kindergarten.repository.material.MaterialRepository;
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
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;


    private final ModelMapper modelMapper;

    private final FileService fileService;

    private final MaterialFileRepository materialFileRepository;

    // 장바구니 구현 2024 11 12
    private final MaterialOrderRepository materialOrderRepository;

    // 장바구니 후처리 구현 2024 11 13
    private final MaterialOrderHistoryRepository orderHistoryRepository;

    //파일 업로드 경로
    @Value("${uploadPathMaterial1}")
    private String uploadPath;

    // 이미지 파일인지 확인하는 메소드
    private boolean isImageFile(String extension) {
        return Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".bmp")
                .contains(extension.toLowerCase());
    }

    //첨부파일이 있는경우 불러오기(다운로드용)
    public MaterialFileDTO getMaterialFile(String fileId) {
        MaterialFileEntity fileEntity = materialFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));

        MaterialFileDTO fileDTO = new MaterialFileDTO();
        String uploadFileName = fileEntity.getOriginalName();
        String encodedUploadFile = UriUtils.encode(uploadFileName, StandardCharsets.UTF_8);

        fileDTO.setFileId(fileEntity.getFileId());
        fileDTO.setOrignalName(encodedUploadFile);
        fileDTO.setFilePath(fileEntity.getFilePath());

        return fileDTO;
    }


    // 글작성 Create
    public String saveMaterial(MaterialFormDTO materialFormDTO){

        // ULID 생성
        Ulid ulid = UlidCreator.getUlid();
        String id = ulid.toString();
        materialFormDTO.setMaterialId(id);
        MaterialEntity materialEntity = materialFormDTO.createMaterial();
        materialRepository.save(materialEntity);

        return materialEntity.getMaterialId();
    }

    // 글작성 Create + 첨부파일 처리
    public void saveMaterialAndFile(MaterialFormDTO materialFormDTO, List<MultipartFile> materialFileList) throws Exception {

        Ulid ulid = UlidCreator.getUlid();
        String id = ulid.toString();
        materialFormDTO.setMaterialId(id);
        MaterialEntity material = materialFormDTO.createMaterial();
        materialRepository.save(material); // 자재 게시물 정보 저장

        if (materialFileList != null && !materialFileList.get(0).isEmpty()){
            boolean isFirstImage = true;
            String mainFileName = null;

            for (MultipartFile file : materialFileList){
                MaterialFileEntity materialFile = new MaterialFileEntity();

                String originalFilename =file.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

                // FileService를 사용하여 파일 저장
                String savedFileName = fileService.uploadFile(uploadPath, originalFilename, file.getBytes());
                String filePath = fileService.getFullPathMaterial(savedFileName);
                log.info("MaterialService.saveMaterialAndFile()메소드 - saveFilename : " + savedFileName);
                log.info("MaterialService.saveMaterialAndFile()메소드 - filePath : " + filePath);

                // 이미지 파일인 경우 첫 번째 파일을 mainFile로 지정
                if (isFirstImage && isImageFile(extension)) {
                    mainFileName = savedFileName;
                    isFirstImage = false;
                }
                // materialFileEntity 설정
                materialFile.setOriginalName(originalFilename);
                materialFile.setModifiedName(savedFileName);
                materialFile.setFilePath(filePath);
                materialFile.setMainFile(mainFileName != null ? mainFileName : ""); // 메인 파일 이름 설정
                log.info("MaterialService.saveMaterialAndFile()메소드 - materialFile 확인 : " + filePath);

                // 새로운 파일 ID 생성
                Ulid fileUlid = UlidCreator.getUlid();
                materialFile.setFileId(fileUlid.toString());

                // moneyEntity 설정
                materialFile.setMaterialEntity((material));

                // 파일 정보 저장
                materialFileRepository.save(materialFile);
            }
        }

    }


    // 자재 Material 상세보기
    @Transactional(readOnly = true)
    public MaterialFormDTO getMaterialDtl(String materialId){

        Optional<MaterialFormDTO> optionalMaterialFormDTO = materialRepository.findById(materialId).map(materialEntity -> modelMapper.map(materialEntity, MaterialFormDTO.class));
        if (optionalMaterialFormDTO.isPresent()) {
            MaterialFormDTO material = optionalMaterialFormDTO.get();
            // material 객체 사용
        } else {
            // 값이 없을 때의 처리 (예: 예외 던지기, 기본값 반환 등)
            throw new NoSuchElementException("No value present for ID: " + materialId);
        }

        MaterialFormDTO materialFormDTO = optionalMaterialFormDTO.get();

        List<MaterialFileEntity> fileEntities = materialFileRepository.findByMaterialEntity_materialId(materialId);
        log.info("파일정보1 : " + fileEntities);
        if (!fileEntities.isEmpty()) {
            List<MaterialFileDTO> fileDTOs = fileEntities.stream()
                    .map(fileEntity -> {
                        MaterialFileDTO fileDTO = new MaterialFileDTO();
                        fileDTO.setFileId(fileEntity.getFileId());
                        fileDTO.setOrignalName(fileEntity.getOriginalName());
                        fileDTO.setModifiedName(fileEntity.getModifiedName());
                        fileDTO.setFilePath(fileEntity.getFilePath());
                        fileDTO.setMainFile(fileEntity.getMainFile());
                        fileDTO.setBoardId(materialId);
                        return fileDTO;
                    })
                    .collect(Collectors.toList());

            materialFormDTO.setMaterialFileList(fileDTOs);
            log.info("파일정보 : " + fileDTOs);
        }

        return materialFormDTO;
    }

    // update 자재 수정 관련 (입력 정보 반영)
    public String updateMoney(MaterialFormDTO materialFormDTO){
        //상품 수정
        MaterialEntity materialEntity = materialRepository.findById(materialFormDTO.getMaterialId())
                .orElseThrow(EntityNotFoundException::new);
        materialEntity.updateMaterial(materialFormDTO);

        return materialEntity.getMaterialId();
    }

    @Transactional(readOnly = true)
    public Page<MaterialEntity> getMaterialPage(MaterialSearchDTO materialSearchDTO, Pageable pageable){
        return materialRepository.getMaterialPage(materialSearchDTO, pageable);
    } // 페이지 처리되는 아이템 처리용


    @Transactional
    public void deleteMaterial(String materialId) throws Exception {
        // 자재 이미지 먼저 삭제
        List<MaterialFileEntity> materialFileEntityList = materialFileRepository.findByMaterialEntity_materialId(materialId); // 메서드 수정 필요
        for(MaterialFileEntity materialFile : materialFileEntityList) {
            fileService.deleteFile(materialFile.getFilePath()); // 실제 이미지 파일 삭제
            materialFileRepository.delete(materialFile); // 이미지 엔티티 삭제
        }

        // 자재 삭제
        MaterialEntity material = materialRepository.findById(materialId)
                .orElseThrow(() -> new EntityNotFoundException("자재를 찾을 수 없습니다."));
        materialRepository.delete(material);
    }
    
    // Material 장바구니 구현

    @Transactional
    public List<MaterialOrderDTO> createOrders(List<String> materialIds, Map<String, String> quantities) {
        List<MaterialOrderDTO> orders = new ArrayList<>();

        for (String materialId : materialIds) {
            MaterialEntity material = materialRepository.findById(materialId)
                    .orElseThrow(() -> new RuntimeException("Material not found"));
                log.info("주문 material 확인 : " + material);
            int quantity = Integer.parseInt(quantities.get("quantities[" + materialId + "]"));

            MaterialOrderEntity order = MaterialOrderEntity.builder()
                    .orderMaterialName(material.getMaterialName())
                    .material(material)
                    .quantity(quantity)
                    .status("PENDING")
                    .build();

            materialOrderRepository.save(order);
            orders.add(convertToDTO(order));
        }

        return orders;
    }

    @Transactional
    public boolean completeOrder(String orderId) {
        MaterialOrderEntity order = materialOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if ("COMPLETED".equals(order.getStatus())) {
            return false;
        }

        MaterialEntity material = order.getMaterial();
        material.setMaterialEa((material.getMaterialEa() + order.getQuantity()));
        materialRepository.save(material);

        order.setStatus("COMPLETED");
        materialOrderRepository.save(order);

        return true;
    }

    // 선택된 자재들 조회
    public List<MaterialDTO> getMaterialsByIds(List<String> materialIds) {
        List<MaterialEntity> materials = materialRepository.findAllById(materialIds);
        return materials.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 모든 주문 조회
    public List<MaterialOrderDTO> getAllOrders() {
        List<MaterialOrderEntity> orders = materialOrderRepository.findAll();
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // MaterialEntity를 MaterialDTO로 변환
    private MaterialDTO convertToDTO(MaterialEntity entity) {
        return MaterialDTO.builder()
                .materialId(entity.getMaterialId())
                .materialName(entity.getMaterialName())
                .materialEa(entity.getMaterialEa())
                //.materialStatus((entity.getMaterialStatus())
                .build();
    }

    // MaterialOrderEntity를 MaterialOrderDTO로 변환
    private MaterialOrderDTO convertToDTO(MaterialOrderEntity entity) {
        return MaterialOrderDTO.builder()
                .orderId(entity.getOrderId())
                .orderMaterialName(entity.getOrderMaterialName())
                .quantity(entity.getQuantity())
                .status(entity.getStatus())
                .orderDate(entity.getOrderDate())
                .build();
    }

    // 장바구니 후처리 구현중 2024 11 13
    // 장바구니 항목 삭제
    public void deleteCartItems(List<String> orderIds) {
        orderIds.forEach(this::deleteCartItem);
    }

    // 장바구니 개별 항목 삭제
    public void deleteCartItem(String orderId) {
        // 장바구니 항목 삭제 로직
        materialOrderRepository.deleteById(orderId);
    }

    // 주문 취소
    public void cancelOrder(Long orderHistoryId) {
        MaterialOrderHistory order = orderHistoryRepository.findById(orderHistoryId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

        order.setMaterialOrderStatus(MaterialOrderStatus.CANCELED);
        order.setStatusChangeDate(LocalDateTime.now());
        orderHistoryRepository.save(order);
    }

    // 주문 이력 조회
    public MaterialOrderHistory getOrderHistory(Long orderId) {
        return orderHistoryRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문 이력을 찾을 수 없습니다."));
    }

    // 주문 이력 삭제
    public void deleteOrderHistory(Long historyId) {
        orderHistoryRepository.deleteById(historyId);
    }

    // 7일 이상 된 주문 이력 삭제
    public void deleteOldOrderHistories() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<MaterialOrderHistory> oldHistories = orderHistoryRepository.findByOrderDateBefore(sevenDaysAgo);
        orderHistoryRepository.deleteAll(oldHistories);
    }

    // 완료/취소된 주문 목록 조회
    public List<MaterialOrderHistory> getCompletedOrCanceledOrders() {
        return orderHistoryRepository.findAllCompletedOrCanceled();
    }

    // 주문 완료 처리
    public void completeOrder(Long orderId) {
        MaterialOrderHistory order = orderHistoryRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

        order.setMaterialOrderStatus(MaterialOrderStatus.COMPLETED);
        order.setStatusChangeDate(LocalDateTime.now());
        orderHistoryRepository.save(order);
    }


}
