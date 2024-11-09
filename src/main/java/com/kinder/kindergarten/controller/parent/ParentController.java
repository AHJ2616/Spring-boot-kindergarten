package com.kinder.kindergarten.controller.parent;

import com.kinder.kindergarten.DTO.parent.ParentErpDTO;
import com.kinder.kindergarten.service.parent.ParentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @PostMapping(value = "/register")
    public String parentRegisterPost(@Valid ParentErpDTO parentErpDTO, BindingResult bindingResult, Model model) {
        // 학부모 등록하기 Post
        log.info("ParentController.parentRegisterPost 메서드 실행중 - - - - - ");

        if (bindingResult.hasErrors()) {
            // 유효성 검사를 해서 오류가 나오면 리턴 시킨다.

            return "parent/parentErpForm";
        }

        try {
            Long parentId = parentService.parentRegister(parentErpDTO);
            // 서비스에서 parentRegister를 parentId로 넣어준다

            return "redirect:/erp/children/register?parentId=" + parentId;
            // 학부모 등록하기가 완료된다면 원아 등록하기로 넘어갈 때 부모 id로 넘어가게 한다.

        } catch (IllegalStateException e) {

            model.addAttribute("errorMessage", e.getMessage());
            // 에러가 나면 에레메세지 표출

            return "parent/parentErpForm";
        }
    }
}
