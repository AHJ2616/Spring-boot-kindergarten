package com.kinder.kindergarten.service.material;



import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;
import com.kinder.kindergarten.DTO.material.*;
import com.kinder.kindergarten.DTO.money.MoneyFormDTO;
import com.kinder.kindergarten.constant.money.MoneyStatus;
import com.kinder.kindergarten.entity.material.MaterialEntity;
import com.kinder.kindergarten.entity.material.MaterialFileEntity;
import com.kinder.kindergarten.entity.material.MaterialOrderEntity;
import com.kinder.kindergarten.entity.material.MaterialOrderHistoryEntity;
import com.kinder.kindergarten.entity.money.MoneyEntity;
import com.kinder.kindergarten.repository.material.MaterialFileRepository;
import com.kinder.kindergarten.repository.material.MaterialOrderHistoryRepository;
import com.kinder.kindergarten.repository.material.MaterialOrderRepository;
import com.kinder.kindergarten.repository.material.MaterialRepository;
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
import java.time.LocalDate;
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

    // OrderHistory 관련 구현 중 2024 11 14
    private final MaterialOrderHistoryRepository materialOrderHistoryRepository;

    // 자재 주문 완료시 회계쪽으로
    private final MoneyRepository moneyRepository;
    
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


    public List<MaterialOrderDTO> getAllOrdersPage() {
        List<MaterialOrderEntity> orders = materialOrderRepository.findAll();
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


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
                    .orderMaterialDetail(material.getMaterialDetail())
                    .orderMaterialPrice(material.getMaterialPrice())
                    .orderMaterialTotalPrice(material.getMaterialPrice()*quantity)
                    .orderWriter(material.getMaterialWriter())
                    .orderWriterEmail(material.getMaterialWriterEmail())
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
    public boolean orderedOrder(String orderId) {
        MaterialOrderEntity order = materialOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if ("COMPLETED".equals(order.getStatus())) {
            return false;
        }

        MaterialEntity material = order.getMaterial();

        order.setStatus("ORDERED");
        materialOrderRepository.save(order);

        return true;
    }

    // 제작중 - 주문 접소 목록에서 입고 완료 버튼 클릭시 동작 - 2024 11 14
    @Transactional
    public boolean completeOrder(String orderId) {

        MaterialOrderEntity order = materialOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        MaterialEntity material = order.getMaterial();
        material.setMaterialEa(material.getMaterialEa() + order.getQuantity());
        materialRepository.save(material);

        MaterialOrderHistoryEntity history = MaterialOrderHistoryEntity.builder()
                .orderMaterialName(order.getOrderMaterialName())
                .orderMaterialDetail(order.getOrderMaterialDetail())
                .orderMaterialPrice(order.getOrderMaterialPrice())
                .orderMaterialTotalPrice(order.getOrderMaterialTotalPrice())
                .quantity(order.getQuantity())
                .status("COMPLETED")
                .orderWriter(order.getOrderWriter())
                .orderWriterEmail(order.getOrderWriterEmail())
                .orderDate(order.getOrderDate())
                .statusChangeDate(LocalDate.now())
                .deletionDate(LocalDateTime.now().plusDays(7))
                .material(order.getMaterial())
                .build();

        Ulid ulid = UlidCreator.getUlid();
        String id = ulid.toString();


        MaterialToMoneyDTO materialToMoneyDTO = MaterialToMoneyDTO.builder()
                .moneyId(id)
                .moneyUseDate(history.getStatusChangeDate())
                .moneyContent(history.getOrderMaterialDetail())
                .moneyWho(history.getOrderWriter())
                .moneyCompany("교내")
                .moneyName(history.getOrderMaterialName())
                .moneyHowMuch(history.getOrderMaterialTotalPrice())
                .moneyStatus(MoneyStatus.EXPENDITURE)
                .moneyApproval("완료")
                .build();

        MoneyEntity moneyEntity = materialToMoneyDTO.createMoney();
        moneyRepository.save(moneyEntity);

        materialOrderHistoryRepository.save(history);
        materialOrderRepository.delete(order);
        // 주문한거 저장


        return true;

    }

    @Transactional
    public boolean cancelOrder(String orderId, String reason) {
        MaterialOrderEntity order = materialOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        MaterialOrderHistoryEntity history = MaterialOrderHistoryEntity.builder()
                .orderMaterialName(order.getOrderMaterialName())
                .orderMaterialDetail(order.getOrderMaterialDetail())
                .orderMaterialPrice(order.getOrderMaterialPrice())
                .quantity(order.getQuantity())
                .orderMaterialTotalPrice(order.getOrderMaterialTotalPrice())
                .status("CANCELED")
                .orderWriter(order.getOrderWriter())
                .orderWriterEmail(order.getOrderWriterEmail())
                .orderDate(order.getOrderDate())
                .statusChangeDate(LocalDate.now())
                .deletionDate(LocalDateTime.now().plusDays(7))
                .material(order.getMaterial())
                .rejectReason(reason)
                .build();

        materialOrderHistoryRepository.save(history);
        materialOrderRepository.delete(order);
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
    public Page<MaterialOrderDTO> getAllOrders(Pageable pageable) {
        Page<MaterialOrderEntity> ordersPage = materialOrderRepository.findAll(pageable);
        return ordersPage.map(this::convertToDTO);
    }

    public Page<MaterialOrderHistoryDTO> getAllOrderHistory(Pageable pageable) {
        Page<MaterialOrderHistoryEntity> ordersHistoryPage = materialOrderHistoryRepository.findAll(pageable);
        return ordersHistoryPage.map(this::convertToDTO);
    }


    // MaterialEntity를 MaterialDTO로 변환
    private MaterialDTO convertToDTO(MaterialEntity entity) {
        return MaterialDTO.builder()
                .materialId(entity.getMaterialId())
                .materialName(entity.getMaterialName())
                .materialDetail(entity.getMaterialDetail())
                .materialEa(entity.getMaterialEa())
                .materialPrice(entity.getMaterialPrice())
                .materialWriter(entity.getMaterialWriter())
                .materialWriterEmail(entity.getMaterialWriterEmail())
                .build();
    }

    // MaterialOrderEntity를 MaterialOrderDTO로 변환
    private MaterialOrderDTO convertToDTO(MaterialOrderEntity entity) {
        return MaterialOrderDTO.builder()
                .orderId(entity.getOrderId())
                .orderMaterialName(entity.getOrderMaterialName())
                .quantity(entity.getQuantity())
                .orderMaterialPrice(entity.getOrderMaterialPrice())
                .orderMaterialTotalPrice(entity.getOrderMaterialTotalPrice())
                .status(entity.getStatus())
                .orderDate(entity.getOrderDate())
                .orderWriter(entity.getOrderWriter())
                .orderWriterEmail(entity.getOrderWriterEmail())
                .build();
    }

    private MaterialOrderHistoryDTO convertToDTO(MaterialOrderHistoryEntity entity) {
        return MaterialOrderHistoryDTO.builder()
                .historyId(entity.getHistoryId())
                .orderMaterialName(entity.getOrderMaterialName())
                .orderMaterialDetail(entity.getOrderMaterialDetail())
                .orderMaterialPrice(entity.getOrderMaterialPrice())
                .quantity(entity.getQuantity())
                .orderMaterialTotalPrice(entity.getOrderMaterialTotalPrice())
                .orderWriter(entity.getOrderWriter())
                .orderWriterEmail(entity.getOrderWriterEmail())
                .status(entity.getStatus())
                .orderDate(entity.getOrderDate())
                .rejectReason(entity.getRejectReason())
                .statusChangeDate(entity.getStatusChangeDate())
                .deletionDate(entity.getDeletionDate())
                .material(entity.getMaterial())
                .build();
    }



    
    // 주문 처리 목록 출력
/*
    public List<MaterialOrderHistory> getOrderHistory() {
        return materialOrderHistoryRepository.findAll();
    }
*/

    // 주문 처리 삭제
    @Transactional
    public void deleteHistoryRecord(String historyId) {
        materialOrderHistoryRepository.deleteById(historyId);
    }



}
