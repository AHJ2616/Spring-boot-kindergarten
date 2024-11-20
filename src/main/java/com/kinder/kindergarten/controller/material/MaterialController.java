package com.kinder.kindergarten.controller.material;

import com.kinder.kindergarten.DTO.employee.EmployeeDTO;
import com.kinder.kindergarten.DTO.material.*;
import com.kinder.kindergarten.DTO.money.MoneyDTO;
import com.kinder.kindergarten.DTO.money.MoneyFormDTO;
import com.kinder.kindergarten.config.PrincipalDetails;
import com.kinder.kindergarten.entity.employee.Employee;
import com.kinder.kindergarten.entity.material.MaterialEntity;
import com.kinder.kindergarten.entity.material.MaterialOrderHistoryEntity;
import com.kinder.kindergarten.service.employee.EmployeeService;
import com.kinder.kindergarten.service.material.MaterialService;
import com.kinder.kindergarten.service.money.MoneyService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Log4j2
@Controller
@RequestMapping(value = "/material")
public class MaterialController {

    private final MaterialService materialService;

    // 로그인 아이디 가져오기 2024 11 18
    public EmployeeService employeeService;

    // 회계관리 자동등록 테스트 2024 11 19
    private final MoneyService moneyService;

    public MaterialController(MaterialService materialService, EmployeeService employeeService, MoneyService moneyService) {
        this.materialService = materialService;
        this.employeeService = employeeService;
        this.moneyService = moneyService;
    }


    // 자재 Create 작성 1
    @GetMapping(value = "/new")
    public String materialForm(@AuthenticationPrincipal PrincipalDetails principalDetails, Model model){

        // loading - login - id
        Employee employee = employeeService.getEmployeeEntity(principalDetails.getEmployee().getId());
        String writer = employee.getMember().getName();
        String writerEmail = employee.getMember().getEmail();

        log.info("materialWriter 값 체크 :" + writer);
        log.info("materialWriterEmail 값 체크 :" + writerEmail);

        model.addAttribute("writer", writer);  // Pass username to Thymeleaf
        model.addAttribute("writerEmail", writerEmail);  // Pass username to Thymeleaf
        model.addAttribute("materialFormDTO", new MaterialFormDTO());

        return "material/materialForm"; // materialForm.html의 경로에 보냄
    }

    // 자재 Create 작성 2
    @PostMapping(value = "/new")
    public String materialNew(@Valid MaterialFormDTO materialFormDTO, BindingResult bindingResult,
                              Model model, @RequestParam(value = "materialFile", required = false)List<MultipartFile> materialFileList){
        try {
            if (bindingResult.hasErrors()){ // 바인딩 에러 체크
                return "material/materialForm";
            }

            // 게시글 저장 방식
            if (materialFileList != null && !materialFileList.isEmpty() && !materialFileList.get(0).isEmpty()){ // 첨부파일 있을경우
                materialService.saveMaterialAndFile(materialFormDTO, materialFileList);
                log.info("첨부파일이 포함된 게시글 작성 입니다.");
            } else {
                materialService.saveMaterial(materialFormDTO); // 첨부파일 없을 경우
                log.info("첨부파일이 없는 게시글 작성 입니다.");

            }

            return "redirect:/material/materials";

        } catch (Exception e){
            log.error("자재 등록중 에러 발생 *MaterialController.materialNew()*: ", e);
            model.addAttribute("errorMessage", "자재 등록 중 에러 발생");
            return "material/materialForm";
        }

    }

    // Read 자재 material 상세 페이지
    @GetMapping(value = "/materialDtl/{materialId}")
    public String materialDtl(Model model, @PathVariable("materialId") String materialId){
        MaterialFormDTO materialFormDTO = materialService.getMaterialDtl(materialId);
        model.addAttribute("materialFormDTO", materialFormDTO);
        return "material/materialDtl";
    }

    // 자재 파일다운로드
    @GetMapping("/download/{materialId}")
    public ResponseEntity<Resource> downloadMaterialFile(@PathVariable String materialId) {
        try {
            MaterialFileDTO fileDTO = materialService.getMaterialFile(materialId);
            Path materialFilePath = Paths.get(fileDTO.getFilePath());
            Resource resource = new UrlResource(materialFilePath.toUri());
            log.error("파일을 찾을 수 없습니다. 경로: {}, 파일명: {}", fileDTO.getFilePath(), fileDTO.getOrignalName());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + fileDTO.getOrignalName() + "\"")
                        .body(resource);
            } else {
                throw new RuntimeException("파일을 찾을 수 없습니다.");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("파일 다운로드 중 오류가 발생했습니다.", e);
        }
    }

    // 자재 관리 목록 보기
    @GetMapping(value = {"/materials", "/materials/{page}"})  //페이징이 없는경우, 있는 경우
    public String materialManage(MaterialSearchDTO materialSearchDTO, @PathVariable("page") Optional<Integer> page, Model model){

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 5);
        // 페이지 파라미터가 없으면 0번 페이지를 보임. 한 페이지당 3개의 상품만 보여줌.
        Page<MaterialEntity> materials = materialService.getMaterialPage(materialSearchDTO, pageable);  // 조회 조건, 페이징 정보를 파라미터로 넘겨서 Page 타입으로 받음
        // 조회 조건과 페이징 정보를 파라미터로 넘겨서 item 객체 받음
        model.addAttribute("materials", materials); // 조회한 상품 데이터 및 페이징정보를 뷰로 전달
        model.addAttribute("materialSearchDTO", materialSearchDTO); // 페이지 전환시 기존 검색 조건을 유지
        model.addAttribute("maxPage", 5);   // 상품관리 메뉴 하단에 보여줄 페이지 번호의 최대 개수 5

        return "material/materialMng";
        // materialMng.html로 리턴함.
    }

    // Update 자재 수정 - 기존 내용 불러오기
    @GetMapping(value = "/{materialId}")
    public String materialEditLoad(@PathVariable("materialId") String materialId, Model model){

        try {
            MaterialFormDTO materialFormDTO = materialService.getMaterialDtl(materialId);
            model.addAttribute("materialFormDTO", materialFormDTO);
        } catch(EntityNotFoundException e){
            model.addAttribute("errorMessage", "존재하지 않는 자재 입니다.");
            model.addAttribute("materialFormDTO", new MaterialFormDTO());
            return "material/materialForm";
        }

        return "material/materialForm";
    }

    // Update 자재 수정 - 수정된 내용 적용
    @PostMapping(value = "/{materialId}")
    public String materialEditWrite(@Valid MaterialFormDTO materialFormDTO, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "material/materialForm";
        }
        materialService.updateMoney(materialFormDTO);

        return "redirect:/material/materials";
    }

    // Delete 자재 삭제
    @PostMapping("/delete/{materialId}")
    public String deleteMaterial(@PathVariable("materialId") String materialId) {
        try {
            materialService.deleteMaterial(materialId);
        } catch (Exception e) {
            // 에러 처리
        }
        return "redirect:/material/materials";
    }

    // 장바구니 구현 2024 11 12

    // 자재관리 메인페이지에서 '주문하기' 누르면 나오는 장바구니
    @GetMapping("/cart")
    public String showCart(@RequestParam String ids, Model model) {
        List<String> materialIds = Arrays.asList(ids.split(","));
        List<MaterialDTO> materials = materialService.getMaterialsByIds(materialIds);
        model.addAttribute("materials", materials);
        return "material/materialCart";
    }

    // 수량입력하고 주문하는 페이지 (장바구니 로 표시됨 현재는)
    @PostMapping("/order")
    public String createOrder(@RequestParam Map<String, String> quantities,
                              @RequestParam List<String> materialIds,
                              RedirectAttributes redirectAttributes) {
        List<MaterialOrderDTO> orders = materialService.createOrders(materialIds, quantities);
        return "redirect:/material/orders";
    }

    // 주문 목록을 보여주는 페이지
    @GetMapping("/orders")
    public String showOrders(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "5") int size,
                             Model model) {
        Page<MaterialOrderDTO> ordersPage = materialService.getAllOrders(PageRequest.of(page, size));
        model.addAttribute("orders", ordersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ordersPage.getTotalPages());
        return "material/materialOrders";
    }

    // 주문 목록에서 PENDDING 상태에서 '주문 하기' 버튼 눌렀을 때.
    @PostMapping("/order/ordered/{orderId}")
    public ResponseEntity<Map<String, Object>> orderOrdered(@PathVariable String orderId) {
        boolean result = materialService.orderedOrder(orderId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", result);
        return ResponseEntity.ok(response);
    }

    // 주문 목록에서 ORDERED 에서 '입고 완료' 버튼 눌렀을 때.
    @PostMapping("/order/completed/{orderId}")
    public ResponseEntity<Map<String, Object>> completeOrder(@PathVariable String orderId) {
        // 주문 완료 표시, orderHistory로 들어감
        boolean result = materialService.completeOrder(orderId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", result);
        return ResponseEntity.ok(response);
    }


    // 주문 목록에서 '주문 반려', '주문 취소' 버튼 눌렀을 때.
    @PostMapping("/order/canceled/{orderId}")
    public ResponseEntity<Map<String, Object>> cancelOrder(@PathVariable String orderId, @RequestBody Map<String, String> body) {
        String reason = body.get("reason"); // 요청에서 반려/취소 사유 추출
        boolean result = materialService.cancelOrder(orderId, reason); // 사유와 함께 서비스 호출
        Map<String, Object> response = new HashMap<>();
        response.put("success", result);
        return ResponseEntity.ok(response);
    }

    // OrderHistory 관련 구현 2024 11 14
    // 자재 History 리스트 출력
    @GetMapping("/history")
    public String showOrderHistory(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "5") int size,
                                   Model model) {
        Page<MaterialOrderHistoryDTO> historiesPage = materialService.getAllOrderHistory(PageRequest.of(page, size));
        model.addAttribute("histories", historiesPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", historiesPage.getTotalPages());
        return "material/materialOrderHistory";
    }

    // materialOrderHistory 삭제
    @PostMapping("/history/delete")
    public String deleteHistoryRecord(@RequestParam String historyId) {
        materialService.deleteHistoryRecord(historyId);
        return "redirect:/material/history";
    }




}
