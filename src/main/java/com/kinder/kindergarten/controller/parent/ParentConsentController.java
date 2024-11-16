package com.kinder.kindergarten.controller.parent;

import com.kinder.kindergarten.DTO.parent.ParentConsentDTO;
import com.kinder.kindergarten.service.parent.ParentConsentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/consent")
@RequiredArgsConstructor
@Log4j2
public class ParentConsentController {

    // 학부모의 동의서에 관한 컨트롤러 ㅇㅇ

    private final ParentConsentService parentConsentService;

    @GetMapping
    public String showConsentForm(  @RequestParam(required = false) Boolean agreed,
                                    @RequestParam(required = false) Boolean communityAgreed,
                                    @RequestParam(required = false) Boolean privacyAgreed,
                                    Model model) {
        // 서비스 이용약관, 커뮤니티 동의서, 개인정보 동의서 페이지를 보여주는 메서드

        try {
            ParentConsentDTO consentDTO = new ParentConsentDTO();

            // 약관 동의 여부 확인
            if (agreed != null && agreed) {
                consentDTO.setTermsConsent(true);
            }
            if (communityAgreed != null && communityAgreed) {
                consentDTO.setCommunityConsent(true);
            }
            if (privacyAgreed != null && privacyAgreed) {
                consentDTO.setPrivateConsent(true);
            }

            model.addAttribute("consent", consentDTO);
            // consentDTO를 모델에 담아서 뷰로 전달

            return "parent/consent/consentForm";

        } catch (Exception e) {

            log.error("동의서 폼 로딩 중 에러 발생: ", e);
            return "error/500";
        }// 동의서 로딩 중 에러가 발생하면 500에러로 리턴
    }

    @PostMapping("/submit")
    public String submitConsent(@Valid @ModelAttribute ParentConsentDTO consentDTO,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        // 약관, 동의서에서 제출 처리 컨트롤러 메서드

        if (bindingResult.hasErrors()) {
            return "parent/consent/consentForm";
        }// 유효성 검사 실패 시 폼으로 리턴

        try {

            if (!isRequiredConsentsChecked(consentDTO)) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "필수 약관에 모두 동의해주셔야 합니다.");
                return "redirect:/consent";
            } // 필수 약관 동의 확인

            // 동의서 저장
            parentConsentService.createConsent(consentDTO);

            // 동의 완료 페이지로 이동
            return "redirect:/consent/success";

        } catch (Exception e) {
            log.error("동의서 처리 중 에러 발생: ", e);
            redirectAttributes.addFlashAttribute("errorMessage", "처리 중 오류가 발생했습니다.");
            return "redirect:/consent";
        }
    }

    @GetMapping("/terms")
    public String showTerms() {
        // ERP 시스템 및 커뮤니티 서비스 이용 약관 페이지 내용 보기 메서드

        return "parent/consent/terms";
    }

    @GetMapping("/community")
    public String showCommunity() {
        // 학부모 커뮤니티 활동 동의서 페이지 내용 보기 메서드

        return "parent/consent/community";
    }

    @GetMapping("/privacy")
    public String showPrivacy() {
        // 학부모 개인정보 및 제3자 제공 동의서 내용 보기 메서드

        return "parent/consent/privacy";
    }

    @GetMapping("/photo")
    public String showPhoto() {
        // 사진 및 영상 촬영/활용 동의서 내용 보기 메서드

        return "parent/consent/photo";
    }

    @GetMapping("/medical")
    public String showMedical() {
        // 의료 정보 활용 동의서 내용보기 메서드

        return "parent/consent/medicalInfo";
    }

    @GetMapping("/emergency")
    public String showEmergency() {
        // 비상 연락망 및 응급 상황 동의서 내용보기 메서드

        return "parent/consent/emergencyInfo";
    }

    // 동의 완료 페이지
    @GetMapping("/success")
    public String showSuccessPage() {
        // 동의 완료한 페이지 보여주는 메서드

        return "parent/consent/success";
    }

    private boolean isRequiredConsentsChecked(ParentConsentDTO consentDTO) {
        // 필수 약관 동의 여부를 확인하는 메서드

        return consentDTO.getTermsConsent() != null && consentDTO.getTermsConsent() &&
                consentDTO.getCommunityConsent() != null && consentDTO.getCommunityConsent() &&
                consentDTO.getPrivateConsent() != null && consentDTO.getPrivateConsent();
    }

    @GetMapping("/second")
    public String showSecondConsentForm(Model model) {
        // 학부모 동의서 2단계 페이지 열어주는 컨트롤러 메세지

        try {
            ParentConsentDTO consentDTO = new ParentConsentDTO();

            model.addAttribute("consent", consentDTO);

            return "parent/consent/consentFormSecond";

        } catch (Exception e) {

            log.error("학부모 동의서 2단계 로딩 중 에러 발생" + e);
            return "error/500";
        }
    }
}
