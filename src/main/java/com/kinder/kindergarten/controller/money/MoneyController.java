package com.kinder.kindergarten.controller.money;

import com.kinder.kindergarten.DTO.employee.EmployeeDTO;
import com.kinder.kindergarten.DTO.money.MoneyFileDTO;
import com.kinder.kindergarten.DTO.money.MoneyFormDTO;
import com.kinder.kindergarten.DTO.money.MoneySearchDTO;
import com.kinder.kindergarten.config.PrincipalDetails;
import com.kinder.kindergarten.entity.employee.Employee;
import com.kinder.kindergarten.entity.money.MoneyEntity;
import com.kinder.kindergarten.service.employee.EmployeeService;
import com.kinder.kindergarten.service.money.ExcelExportService;
import com.kinder.kindergarten.service.money.MoneyChartService;
import com.kinder.kindergarten.service.money.MoneyService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.YearMonth;
import java.util.*;

@Log4j2
@Controller
@RequestMapping(value = "/money")
public class MoneyController {

    private final MoneyService moneyService;

    // 차트 만들기 - 테스트 진행중 2024 11 07
    private final MoneyChartService moneyChartService;

    // 엑셀 형식 출력 - 테스트 진행중 2024 11 09
    private final ExcelExportService excelExportService;

    public EmployeeService employeeService;

    public MoneyController(MoneyService moneyService, MoneyChartService moneyChartService,
                           ExcelExportService excelExportService, EmployeeService employeeService) {
        this.moneyService = moneyService;
        this.moneyChartService = moneyChartService;
        this.excelExportService = excelExportService;
        this.employeeService = employeeService;
    }


    // 회계 Create 작성 1
    @GetMapping(value = "/new")
    public String moneyForm(@AuthenticationPrincipal PrincipalDetails principalDetails,
                            Model model){
        model.addAttribute("moneyFormDTO", new MoneyFormDTO());

        // 로그인 세션값
        Employee employee = employeeService.getEmployeeEntity(principalDetails.getEmployee().getId());
        String moneyWriter = employee.getMember().getName();

        model.addAttribute("moneyWriter", moneyWriter);  // Pass username to Thymeleaf


        return "money/moneyForm"; // moneyForm.html의 경로에 보냄
    }

    // 회계 Creat 작성 2
    @PostMapping(value = "/new")
    public String moneyNew(@Valid MoneyFormDTO moneyFormDTO, BindingResult bindingResult,
                           Model model, @RequestParam(value = "moneyFile", required = false)List<MultipartFile> moneyFileList){
        try {
            if (bindingResult.hasErrors()){ // 바인딩 에러 체크
                return "money/moneyForm";
            }

            // 게시글 저장 방식
            if (moneyFileList != null && !moneyFileList.isEmpty() && !moneyFileList.get(0).isEmpty()){ // 첨부파일 있을경우
                moneyService.saveMoneyAndFile(moneyFormDTO, moneyFileList);
            } else {
                moneyService.saveMoney(moneyFormDTO); // 첨부파일 없을 경우
            }

            return "redirect:/money/moneys";

        } catch (Exception e){
            log.error("회계 내역 등록중 에러 발생 *MoneyController.moneyNew()*: ", e);
            model.addAttribute("errorMessage", "회계 내역 등록 중 에러 발생");
            return "money/moneyForm";
        }

    }

    // Read 회계 Money 상세 페이지
    @GetMapping(value = "/moneyDtl/{moneyId}")
    public String moneyDtl(Model model, @PathVariable("moneyId") String moneyId){
        MoneyFormDTO moneyFormDTO = moneyService.getMoneyDtl(moneyId);
        model.addAttribute("moneyFormDTO", moneyFormDTO);
        return "money/moneyDtl";
    }

    // 회계 파일다운로드
    @GetMapping("/download/{moneyId}")
    public ResponseEntity<Resource> downloadMoneyFile(@PathVariable String moneyId) {
        try {
            MoneyFileDTO fileDTO = moneyService.getMoneyFile(moneyId);
            Path moneyFilePath = Paths.get(fileDTO.getFilePath());
            Resource resource = new UrlResource(moneyFilePath.toUri());
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

    // 회계 목록 보기
    @GetMapping(value = {"/moneys", "/moneys/{page}"})  //페이징이 없는경우, 있는 경우
    public String moneyList(MoneySearchDTO moneySearchDTO , @PathVariable("page") Optional<Integer> page, Model model){

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 5);
        // 페이지 파라미터가 없으면 0번 페이지를 보임. 한 페이지당 3개의 상품만 보여줌.
        Page<MoneyEntity> moneys = moneyService.getMoneyPage(moneySearchDTO, pageable);  // 조회 조건, 페이징 정보를 파라미터로 넘겨서 Page 타입으로 받음
        // 조회 조건과 페이징 정보를 파라미터로 넘겨서 item 객체 받음
        model.addAttribute("moneys", moneys); // 조회한 정보 데이터 및 페이징정보를 뷰로 전달
        model.addAttribute("moneySearchDTO", moneySearchDTO); // 페이지 전환시 기존 검색 조건을 유지
        model.addAttribute("maxPage", 5);   // 상품관리 메뉴 하단에 보여줄 페이지 번호의 최대 개수 5

        // 결재 상태별 건수 추가
        model.addAttribute("approvalCounts", moneyService.countByApprovalStatus());
        
        return "money/moneyMng";
        // materialMng.html로 리턴함.
    }

    // 기존에 입력값이 있을 경우 Form 에서 내용 불러옴.
    // Update 회계 수정 - 기존 내용 불러오기
    @GetMapping(value = "/{moneyId}")
    public String moneyEditLoad(@PathVariable("moneyId") String moneyId, Model model){

        try {
            MoneyFormDTO moneyFormDTO = moneyService.getMoneyDtl(moneyId);
            model.addAttribute("moneyFormDTO", moneyFormDTO);
        } catch(EntityNotFoundException e){
            model.addAttribute("errorMessage", "존재하지 않는 금액 내역 입니다.");
            model.addAttribute("moneyFormDTO", new MoneyFormDTO());
            return "money/moneyForm";
        }

        return "money/moneyForm";
    }

    // Update 회계 수정 - 수정된 내용 적용
    @PostMapping(value = "/{moneyId}")
    public String moneyEditWrite(@Valid MoneyFormDTO moneyFormDTO, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            return "money/moneyForm";
        }
        moneyService.updateMoney(moneyFormDTO);

        return "redirect:/money/moneys";
    }

    // Delete 회계 삭제 - moneyNng.html - Ajax 처리
    @PostMapping("delete/{moneyId}")
    public String deleteMoney(@PathVariable("moneyId") String moneyId){
        try {
            moneyService.deleteMoney(moneyId);
        } catch (Exception e) {
            // 에러 처리
        }
        return "redirect:/money/moneys";
    }

    // modal 실험 moneyMng.html
    @GetMapping("/moneyModalRead")
    @ResponseBody
    public MoneyFormDTO moneyModalRead(@RequestParam Map<String, Object> param){
        log.info("모달창 정보 업로드 : MoneyController.moneyModalRead() 메소드");
        String moneyId = (String) param.get("moneyId");

        MoneyFormDTO dto = moneyService.getMoneyDtl(moneyId);

        return dto;
    }

    @PostMapping("/moneyModalSave")
    public String moneyModalSave(MoneyFormDTO moneyFormDTO){
        log.info("모달창 정보 수정 : MoneyController.moneyModalSave() 메소드");

        log.info("모달창 정보 수정 : moneyFormDTO : " + moneyFormDTO.getMoneyId());
        log.info("모달창 정보 수정 : moneyFormDTO : " + moneyFormDTO.getMoneyApproval());
        log.info("모달창 정보 수정 : moneyFormDTO : " + moneyFormDTO.getMoneyName());
        log.info("모달창 정보 수정 : moneyFormDTO : " + moneyFormDTO.getMoneyCompany());

        moneyService.updateMoney(moneyFormDTO);


        return "redirect:/money/moneys";
    }

    // 차트 만들기 구현중 - 테스트
    @GetMapping("/chart")
    public String showChart() {
        return "money/moneyChart";
    }

    @GetMapping("/api/chart-data")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getChartData(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        Map<String, Object> chartData = moneyChartService.getMonthlyChartData(month);
        return ResponseEntity.ok(chartData);
    }


    // 엑셀 형식으로 다운로드하기
    @GetMapping("/download/excel")
    public ResponseEntity<byte[]> downloadExcel() {
        try {
            List<MoneyFormDTO> moneyList = moneyService.getAllMoneyData(); // 모든 데이터 가져오기
            byte[] excelFile = excelExportService.exportMoneyListToExcel(moneyList);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "money_management.xlsx");

            return new ResponseEntity<>(excelFile, headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

}
