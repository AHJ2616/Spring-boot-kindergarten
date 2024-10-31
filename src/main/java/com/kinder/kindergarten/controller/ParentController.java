package com.kinder.kindergarten.controller;

import com.kinder.kindergarten.DTO.EmailFindDTO;
import com.kinder.kindergarten.DTO.ParentFormDTO;
import com.kinder.kindergarten.DTO.ParentUpdateFormDTO;
import com.kinder.kindergarten.entity.Parent;
import com.kinder.kindergarten.service.ParentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/parents")
public class ParentController {


    private final ParentService parentService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ParentController(ParentService parentService, PasswordEncoder passwordEncoder) {
        this.parentService = parentService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping(value = "/new")
    public String parentNew(Model model) {
        // 학부모 회원 가입 페이지 경로

        model.addAttribute("parentFormDTO", new ParentFormDTO());

        return "parent/parentForm";
    }


    @PostMapping(value = "/new")
    public String parentNew(@Valid ParentFormDTO parentFormDTO, BindingResult bindingResult, Model model) {
        // 학부모 회원 가입 페이지에서 PostMapping

        if (bindingResult.hasErrors()) {
            return "parent/parentForm";
        }// 입력 유효성 검사에서 오류가 발생하면? 폼으로 리턴

        if (!parentFormDTO.getParent_pw().equals(parentFormDTO.getParent_pw2())) {
            // parentFormDTO에서 패스워드 1과 2가 같지 않다면 ? 오류 메세지 발생

            bindingResult.rejectValue("parent_pw2", "passwordInCorrect", "2개의 비밀번호가 맞지 않습니다.");
            // 비밀번호가 일치 하지 않을 경우 오류를 발생하게 한다.
            // bindingResult.rejectValue -> (필드명, 오류코드, 오류메시지) 의미

            return "parent/parentForm";  // 오류가 발생한 경우 다시 폼 페이지로 이동
        }

        try {
            Parent parent = Parent.createParent(parentFormDTO, passwordEncoder);
            parentService.saveParent(parent);
            // Parent 객체 생성 및 비밀번호 암호화 후 저장
        } catch (IllegalStateException e) {

            model.addAttribute("errorMessage", e.getMessage());

            return  "parent/parentForm";
        }

        return "redirect:/";// 성공적으로 등록이 됐다면 리다이렉트
        //  10.29 -> 메인페이지 미작성으로 404 오류페이지 뜲, DB는 OK
    }
    // 메인 페이지 이동 하는 페이지, 컨트롤러 미작성

    @GetMapping(value = "/login")
    public String parentLogin() {
        // 학부모 로그인 페이지

        return "/parent/parentLoginForm";
    }

    @GetMapping(value = "/login/error")
    public String loginError(Model model) {
        // 학부모 로그인 페이지에서 에러 날 경우

        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해 주시길 바랍니다.");
        // 에러 메세지 표출
        return "/parent/parentLoginForm";
    }


    @GetMapping(value = "/{email}/update")
    public String parentUpdate(@PathVariable String email, Model model) {
        // 학부모의 정보 수정 페이지 경로

        Parent parent = parentService.parentEmailGet(email);
        // 이메일로 학부모 데이터 정보 조회하기

        ParentUpdateFormDTO parentUpdateFormDTO = new ParentUpdateFormDTO();
        parentUpdateFormDTO.setParent_name(parent.getParent_name());
        parentUpdateFormDTO.setParent_email(parent.getParent_email());
        parentUpdateFormDTO.setParent_phone(parent.getParent_phone());
        parentUpdateFormDTO.setParent_address(parent.getParent_address());
        // 해당되는 계정의 기존 정보를 DTO에 담기

        model.addAttribute("parentUpdateForm", parentUpdateFormDTO);
        // 모델에다가 추가
        return "/parent/parentUpdateForm";

    }

    @PostMapping(value = "/{email}/update")
    public String parentUpdatePost(@PathVariable String email, @Valid @ModelAttribute ParentUpdateFormDTO parentUpdateFormDTO,
                                   BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "parent/parentUpdateForm";
        } // 유효성 검사에서 오류가 발생하면 폼으로 리턴

        try {
            Parent updatedParent = Parent.updateParent(parentUpdateFormDTO);
            parentService.updateParent(email, updatedParent);
            return "redirect:/";

        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "parent/parentUpdateForm";
        }
    }

    @PostMapping(value = "/{email}/delete")
    public String parentDelete(@PathVariable String email) {
        // 학부모의 탈퇴 처리(이메일 기준)

        parentService.deleteParent(email);

        return "redirect:/parents";
    }

    @GetMapping("/emailFind")
    public String parentEmailFind(Model model) {
        // 이메일(계정) 찾기 경로 설정

        model.addAttribute("emailFindDTO", new EmailFindDTO());
        // EmailFindDTO를 모델에 담아서 뷰로 보낸다.

        return "parent/parentEmailFind";
    }

    @PostMapping("/emailFind")
    public String parentEmailFindPost(@Valid @ModelAttribute EmailFindDTO emailFindDTO, BindingResult bindingResult, Model model) {
    // 이메일(계정) 페이지의 PostMapping

        if (bindingResult.hasErrors()) {
            return "parent/parentEmailFind";
        }// 유효성 검사에서 오류가 발생하면 리턴한다.

        String foundEmail = parentService.findParentEmail(emailFindDTO);
        //foundEmail 이라는 변수에 서비스에 있는 학부모 이메일 찾기 기능 메서드를 불러온다.

        if (foundEmail == null) {
            // 찾을려는 이메일(계정)의 정보가 없으면?

            model.addAttribute("errorMessage", "입력하신 정보와 일치하는 계정이 없습니다.");
            // 모델에다가 에러메세지를 표출한다.

            return "parent/parentEmailFind";
        }

        model.addAttribute("foundEmail", foundEmail);
        // 찾을려는 이메일(계정) 정보가 있다면 모델에 담아서 뷰로 전달해 주고

        return "parent/parentEmailResult";  // 결과 페이지로 이동
    }

    @GetMapping(value = "/changePassword")
    public String parentPwChange() {
        // 비밀번호 찾기 경로 설정

        return "parent/changePassword";
    }

    @PostMapping(value = "/changePassword")
    public String parentPwChangePost(@RequestParam String email, Model model) {
        // 비밀번호 찾기 PostMappling

        try {
            String tempPassword = parentService.passwordChange(email);
            // 서비스에서 비밀번호 찾기 메서드를 tempPassword 변수에 꽂는다.

            model.addAttribute("tempPassword","임시 비밀번호가 발급 되었습니다." + tempPassword);

            return "parent/changePwResult";
            // 임시 비밀번호가 발급이 되면 임시 비밀번호 발급 페이지로 리턴

        } catch (IllegalArgumentException e) {

            model.addAttribute("message","입력하신 정보와 일치하는 계정이 없습니다.");

            return "parent/changePassword";
        }


    }


}

