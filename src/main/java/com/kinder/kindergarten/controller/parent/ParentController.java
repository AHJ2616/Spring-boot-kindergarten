package com.kinder.kindergarten.controller.parent;

import com.kinder.kindergarten.DTO.parent.ParentErpDTO;
import com.kinder.kindergarten.DTO.parent.ParentUpdateDTO;
import com.kinder.kindergarten.constant.parent.ParentType;
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

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/erp/parent")
@RequiredArgsConstructor
@Log4j2
public class ParentController {

    private final ParentService parentService;

    @GetMapping(value = "/register")
    public String parentRegister(Model model) {
        // 학부모 등록하기 GetMapping

        log.info("ParentController.parentRegister 메서드 실행중  - - - - - - -");

        model.addAttribute("parentErpDTO", new ParentErpDTO());
        // 부모의 정보가 담긴 parentErpDTO를 모델에 담아서 뷰로 전달~

        return "parent/parentErpForm";
    }

    @PostMapping(value = "/register", produces = "application/json")// Post방식으로 처리하고, Json 방식으로 응답을 생성
    @ResponseBody// Json 응답을 받기 위해 @ResponseBody 지정
    public ResponseEntity<?> parentRegisterPost(@Valid @ModelAttribute ParentErpDTO parentErpDTO, BindingResult bindingResult) {
        log.info("ParentController.parentRegisterPost 메서드 실행중  - - - - - - -");

        try {
            log.info("Received DTO: {}", parentErpDTO);
            log.info("Phone: {}", parentErpDTO.getParentPhone());
            log.info("Email: {}", parentErpDTO.getParentEmail());
            log.info("Name: {}", parentErpDTO.getParentName());
            log.info("Type: {}", parentErpDTO.getParentType());

            if (bindingResult.hasErrors()) {
                // 유효성 검사에서 에러가 난다면 ?

                Map<String, Object> response = new HashMap<>();// 맵 형식 객체 생성
                response.put("success", false);

                Map<String, String> errors = bindingResult.getFieldErrors().stream()
                        .collect(Collectors.toMap(
                                // 에러 메세지를 모아서 Map으로 변환한다.

                                error -> error.getField(),// 에러가 발생한 필드명
                                error -> error.getDefaultMessage()// 에러 발생 메세지
                        ));
                response.put("errors", errors);
                return ResponseEntity.badRequest().body(response);// 400에러로 응답해준다.
            }

            Long parentId = parentService.parentRegister(parentErpDTO);
            // 학부모 등록하기 서비스 메서드를 불러와서 parentId 꽂아준다.
            log.info("불러온 부모ID가 누구니? " +  parentId);

            Map<String, Object> response = new HashMap<>();// 맵 형식 객체 생성

            response.put("success", true);
            response.put("parentId", parentId);
            response.put("tempPassword", parentErpDTO.getTempPassword());
            // 성공한 응답 데이터인 부모ID와 패스워드를 맵에다가 꽂아주고

            return ResponseEntity.ok(response);// 200 OK 응답

        } catch (IllegalStateException e) {
            log.error("에러가 발생 했습니다 : ", e);

            Map<String, Object> response = new HashMap<>();// 맵 형식 객체 생성

            response.put("success", false);
            response.put("error", e.getMessage());
            // 실패한 에러 데이터들을 맵에다가 꽂아 주고

            return ResponseEntity.badRequest().body(response);// 400 Bad Request 응답

        } catch (Exception e) {
            log.error("부모 등록 중 예상치 못한 오류가 발생했습니다.: ", e);

            Map<String, Object> response = new HashMap<>();// 맵 형식 객체 생성

            response.put("success", false);
            response.put("error", "학부모 등록 중 오류가 발생했습니다.");
            // 또 다른 실패한 에러 데이터들도 맵에다가 꽂아주고

            return ResponseEntity.internalServerError().body(response);// 400 Bad Request 응답
        }
    }

    @GetMapping("/list")
    public String parentList(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
                             @RequestParam(required = false) String keyword, Model model) {
        // 등록된 학부모의 리스트의 경로 메서드

        Pageable pageable = PageRequest.of(page, size);
        Page<ParentErpDTO> parents;

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

        try {
            ParentErpDTO parentErpDTO  = parentService.getParentDetail(parentId);
            // 상세보기 서비스 메서드를 호출하여 DTO에다가 꽂아준다.

            ParentUpdateDTO parentUpdateDTO = new ParentUpdateDTO();// 업데이트 할 데이터를 담는 집을 만들어 주고

            parentUpdateDTO.setParentId(parentId);
            parentUpdateDTO.setParentName(parentErpDTO.getParentName());
            parentUpdateDTO.setParentPhone(parentErpDTO.getParentPhone());
            parentUpdateDTO.setChildrenEmergencyPhone(parentErpDTO.getChildrenEmergencyPhone());
            parentUpdateDTO.setParentAddress(parentErpDTO.getParentAddress());
            parentUpdateDTO.setParentType(parentErpDTO.getParentType());
            // 업데이트 할 각종 정보들을 지정해준다.

            model.addAttribute("parent", parentUpdateDTO);
            model.addAttribute("parentTypes", ParentType.values());
            // 지정해준 DTO를 모델에 담아서 뷰로 전달해준다.(부모의 타입과 함께)

            return "parent/parentModify";

        } catch (Exception e) {

            log.error("학부모 수정 조회 중 오류 발생 !", e.getMessage(), e);
            // 에러가 발생 시 메세지 표출

            return "redirect:/erp/parent/list";
        }
    }

    @PostMapping("/modify/{parentId}")
    public String modifyParentPost(@PathVariable Long parentId, @ModelAttribute("parent") @Valid ParentUpdateDTO parentUpdateDTO,
                                   BindingResult bindingResult, Model model) {
        // 학부모 정보 수정할 때 POST 처리 메서드

        log.info("수정 요청 받은 부모ID" + parentId);
        log.info("수정한 DTO : " + parentUpdateDTO);

        if (bindingResult.hasErrors()) {
            log.error("유효성 검사 실패: {}", bindingResult.getAllErrors());

            model.addAttribute("parentTypes", ParentType.values());
            return "parent/parentModify";
        }

        try {
            parentUpdateDTO.setParentId(parentId);
            log.info("수정 서비스 호출 전 DTO: {}", parentUpdateDTO);
            parentService.updateParent(parentUpdateDTO);
            log.info("수정 완료");
            return "redirect:/erp/parent/detail/" + parentId;

        } catch (Exception e) {

            log.error("학부모 수정 조회 중 오류 발생 !", e.getMessage(), e);
            model.addAttribute("parentTypes", ParentType.values());
            model.addAttribute("errorMessage", "수정 중 오류가 발생했습니다: ");

            return "parent/parentModify";
        }
    }

    @PostMapping("/delete/{parentId}")
    public String deleteParentPost(@PathVariable Long parentId, Model model) {

        try {
            parentService.deleteParent(parentId);

            return "redirect:/erp/parent/list";

        } catch (EntityNotFoundException e) {

            model.addAttribute("errorMessage", "학부모를 찾을 수 없습니다.");

            return "redirect:/erp/parent/list";

        } catch (Exception e) {
            log.error("학부모 삭제 중 오류 발생: ", e);

            model.addAttribute("errorMessage", "삭제 중 오류가 발생했습니다.");

            return "redirect:/erp/parent/list";
        }
    }


}
