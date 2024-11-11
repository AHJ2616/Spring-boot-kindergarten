package com.kinder.kindergarten.controller.parent;

import com.kinder.kindergarten.DTO.parent.ParentErpDTO;
import com.kinder.kindergarten.service.parent.ParentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
    @ResponseBody// 응답을 받기 위해 @ResponseBody 지정
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
}
