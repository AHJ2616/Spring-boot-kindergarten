package com.kinder.kindergarten.controller.parent;

import com.kinder.kindergarten.DTO.parent.ParentErpDTO;
import com.kinder.kindergarten.DTO.parent.ParentUpdateDTO;
import com.kinder.kindergarten.constant.parent.ParentType;
import com.kinder.kindergarten.entity.parent.Parent;
import com.kinder.kindergarten.repository.parent.ParentRepository;
import com.kinder.kindergarten.service.parent.ParentService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/erp/parent")
@RequiredArgsConstructor
@Log4j2
public class ParentController {

    private final ParentService parentService;

    private final ParentRepository parentRepository;

    @GetMapping(value = "/register")
    public String parentRegister(Model model) {
        // ERP에서 학부모 등록하기 페이지 보여주는 메서드

        log.info("ParentController.parentRegister 메서드 실행중  - - - - - - -");

        model.addAttribute("parentErpDTO", new ParentErpDTO());
        // 부모의 정보가 담긴 parentErpDTO를 모델에 담아서 뷰로 전달~

        return "parent/parentErpForm";
    }

    @PostMapping(value = "/register", produces = "application/json")// Post 요청을 JSON 형식으로 응답한다.
    @ResponseBody// JSON 응답을 할려면 @ResponseBody 이용해야 한다.
    public ResponseEntity<?> parentRegisterPost(@Valid @ModelAttribute ParentErpDTO parentErpDTO, BindingResult bindingResult) {
        //ERP에서 학부모 등록하기 Post 메서드

        log.info("학부모 등록 요청 데이터:  " + parentErpDTO);

        try {
            // 유효성 검사 오류가 있다면?
            if (bindingResult.hasErrors()) {

                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                // 필드 오류들을 MAP으로 변환(필드명 -> 오류메세지)

                Map<String, String> errors = bindingResult.getFieldErrors().stream()
                        .collect(Collectors.toMap(
                                error -> error.getField(),// 오류가 발생한 필드명
                                error -> error.getDefaultMessage()// 오류 메세지
                        ));
                response.put("errors", errors);
                return ResponseEntity.badRequest().body(response);
                // 유효성 검사에서 오류가 발생한다면 400 에러로 응답하게 한다.

            }

            // 필수 필드 검증
            if (parentErpDTO.getEmail() == null || parentErpDTO.getName() == null ||
                    parentErpDTO.getPhone() == null || parentErpDTO.getAddress() == null) {
                // 이메일, 성함, 연락처, 주소 필수 필드가 Null인지 검사한다.

                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", "필수 정보가 누락되었습니다.");
                // MAP 으로 변환

                return ResponseEntity.badRequest().body(response);
                // 필수 필드 검증 에서 NULL이 있다고 판단이 되면 400에러로 응답하게 한다.
            }

            Long parentId = parentService.parentRegister(parentErpDTO);
            // 앞서 검사 단계에서 이상이 없으면 서비스에서 학부모 등록 처리 메서드를 불러온다.


            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("parentId", parentId);// 검사 단계에서 이상이 없고 새로 생성된 학부모ID
            response.put("tempPassword", parentErpDTO.getTempPassword());// 임시 비밀번호
            // 그리고 데이터를 구성하고 성공 응답한다.

            return ResponseEntity.ok(response);
            // 성공 응답으로 200 으로 표출

        } catch (IllegalStateException e) {
            log.error("학부모 등록 중 비즈니스 로직 오류: ", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
            // 중복된 이메일이 발생된 경우 로직 오류

        } catch (Exception e) {
            log.error("학부모 등록 중 예상치 못한 오류: ", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "학부모 등록 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
            // 그 외 기타 예상치 못한 오류 발생으로 500에러 표출
        }
    }

    @GetMapping("/list")
    public String parentList(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
                             @RequestParam(required = false) String keyword, Model model) {
        // ERP에서 등록된 학부모의 리스트의 경로 메서드

        Pageable pageable = PageRequest.of(page, size);
        Page<ParentErpDTO> parents;
        // ParentErpDTO 의 학부모 데이터를 페이징 처리하기 위해 생성

        if (keyword != null && !keyword.trim().isEmpty()) {
            parents = parentService.searchParents(keyword, pageable);

        }else {
            parents = parentService.getAllParents(pageable);
        }

        model.addAttribute("parents", parents);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPage", parents.getTotalPages());
        model.addAttribute("keyword", keyword);
        // 모델에다가 학부모, 현재 페이지, 모든 페이지, 키워드 정보들을 담아서 뷰로 전달한다.

        return "parent/parentList";
    }

    @GetMapping("/detail/{parentId}")
    public String parentDetail(@PathVariable Long parentId, Model model) {
        // 학부모 상세 정보 경로 메서드(부모의 ID를 매개값 지정)

        try {
            ParentErpDTO parentErpDTO = parentService.getParentDetail(parentId);
            // 학부모DTO를 서비스에서 상세보기 기능하는 서비스 메서드를 호출해서 DTO변수에다가 꽂아준다.

            model.addAttribute("parent", parentErpDTO);
            // 꽂아준 DTO를 모델에 담아서 뷰로 전달한다.

            return "parent/parentDetail";

        } catch (Exception e) {

            log.error("학부모 상세 정보 조회 중 오류 발생 !", e);
            // 오류 발생 시 에러 메세지 표출

            return "redirect:/erp/parent/list";
        }
    }

    @GetMapping("/modify/{parentId}")
    public String modifyParent(@PathVariable Long parentId, Model model) {
        // 학부모의 정보를 수정하는 경로

        log.info("ParentController.modifyParent 메서드 실행중  = = = = = =" + parentId);

        try {
            // 학부모 상세 정보 조회 (Member + Parent 정보 포함)
            ParentErpDTO parentErpDTO = parentService.getParentDetail(parentId);
            log.info("조회된 학부모 정보: {}", parentErpDTO);

            // 수정용 DTO 생성 및 데이터 설정
            ParentUpdateDTO updateDTO = new ParentUpdateDTO();
            updateDTO.setParentId(parentId);
            updateDTO.setName(parentErpDTO.getName());
            updateDTO.setPhone(parentErpDTO.getPhone());
            updateDTO.setAddress(parentErpDTO.getAddress());
            updateDTO.setChildrenEmergencyPhone(parentErpDTO.getChildrenEmergencyPhone());
            updateDTO.setParentType(parentErpDTO.getParentType());

            model.addAttribute("parent", updateDTO);
            model.addAttribute("parentTypes", ParentType.values());

            return "parent/parentModify";

        } catch (EntityNotFoundException e) {
            log.error("학부모 정보를 찾을 수 없음 - ID: {}", parentId, e);
            return "redirect:/erp/parent/list";
        } catch (Exception e) {
            log.error("학부모 수정 폼 로딩 중 오류 발생", e);
            return "redirect:/erp/parent/list";
        }

    }

    @PostMapping("/modify/{parentId}")
    public String modifyParentPost(@PathVariable Long parentId,
                                   @Valid @ModelAttribute("parent") ParentUpdateDTO updateDTO,
                                   BindingResult bindingResult,
                                   Model model) {
        log.info("학부모 정보 수정 요청 - ID: {}, Data: {}", parentId, updateDTO);

        if (bindingResult.hasErrors()) {
            log.error("유효성 검사 실패: {}", bindingResult.getAllErrors());
            model.addAttribute("parentTypes", ParentType.values());
            return "parent/parentModify";
        }

        try {
            updateDTO.setParentId(parentId);
            parentService.updateParent(updateDTO);
            log.info("학부모 정보 수정 완료 - ID: {}", parentId);

            return "redirect:/erp/parent/detail/" + parentId;
        } catch (Exception e) {
            log.error("학부모 정보 수정 중 오류 발생", e);
            model.addAttribute("parentTypes", ParentType.values());
            model.addAttribute("errorMessage", "수정 중 오류가 발생했습니다: " + e.getMessage());
            return "parent/parentModify";
        }
    }

    @PostMapping("/delete/{parentId}")
    public String deleteParentPost(@PathVariable Long parentId, RedirectAttributes redirectAttributes) {
        log.info("학부모 삭제 요청 - ID: {}", parentId);

        try {
            // 학부모 정보 존재 여부 확인
            Optional<Parent> parentOptional = parentRepository.findById(parentId);
            if (parentOptional.isEmpty()) {
                log.error("학부모 정보를 찾을 수 없음 - ID: {}", parentId);
                redirectAttributes.addFlashAttribute("error", "삭제할 학부모 정보를 찾을 수 없습니다.");
                return "redirect:/erp/parent/list";
            }

            Parent parent = parentOptional.get();
            log.info("삭제할 학부모 정보: {}", parent);

            // 삭제 처리
            try {
                parentService.deleteParent(parentId);
                log.info("학부모 삭제 성공 - ID: {}", parentId);
                redirectAttributes.addFlashAttribute("message", "학부모 정보가 삭제되었습니다.");
            } catch (Exception e) {
                log.error("학부모 삭제 중 오류 발생: ", e);
                redirectAttributes.addFlashAttribute("error", "삭제 처리 중 오류가 발생했습니다.");
            }

            return "redirect:/erp/parent/list";

        } catch (Exception e) {
            log.error("예상치 못한 오류 발생: ", e);
            redirectAttributes.addFlashAttribute("error", "시스템 오류가 발생했습니다.");
            return "redirect:/erp/parent/list";
        }
    }


}
