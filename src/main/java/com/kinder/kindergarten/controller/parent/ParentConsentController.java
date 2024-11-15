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
            return "parent/consent/consentForm";
        } catch (Exception e) {
            log.error("동의서 폼 로딩 중 에러 발생: ", e);
            return "error/500";
        }
    }

    // 약관 동의 처리
    @PostMapping("/submit")
    public String submitConsent(@Valid @ModelAttribute ParentConsentDTO consentDTO,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "parent/consent/consentForm";
        }

        try {
            // 필수 약관 동의 확인
            if (!isRequiredConsentsChecked(consentDTO)) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "필수 약관에 모두 동의해주셔야 합니다.");
                return "redirect:/consent";
            }

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

    // 약관 내용 보기
    @GetMapping("/terms")
    public String showTerms() {
        return "parent/consent/terms";
    }

    @GetMapping("/community")
    public String showCommunity() {
        return "parent/consent/community";
    }

    @GetMapping("/privacy")
    public String showPrivacy() {
        return "parent/consent/privacy";
    }

    // 동의 완료 페이지
    @GetMapping("/success")
    public String showSuccessPage() {
        return "parent/consent/success";
    }

    // 필수 약관 동의 확인
    private boolean isRequiredConsentsChecked(ParentConsentDTO consentDTO) {
        return consentDTO.getTermsConsent() != null && consentDTO.getTermsConsent() &&
                consentDTO.getCommunityConsent() != null && consentDTO.getCommunityConsent() &&
                consentDTO.getPrivateConsent() != null && consentDTO.getPrivateConsent();
    }
}
