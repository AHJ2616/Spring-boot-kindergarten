package com.kinder.kindergarten.controller.parent;

import com.kinder.kindergarten.DTO.parent.ParentConsentDTO;
import com.kinder.kindergarten.service.parent.ParentConsentService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.catalina.Session;
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
    public String showConsentForm(HttpSession session, Model model) {
        ParentConsentDTO consentDTO = (ParentConsentDTO) session.getAttribute("consentStates");
        if (consentDTO == null) {
            consentDTO = new ParentConsentDTO();
        }
        model.addAttribute("consent", consentDTO);
        return "parent/consent/consentForm";
    }

    // 1단계 동의서 매핑 추가
    @GetMapping("/terms")
    public String showTerms(HttpSession session, Model model) {
        ParentConsentDTO consentDTO = (ParentConsentDTO) session.getAttribute("consentStates");
        model.addAttribute("consent", consentDTO);
        return "parent/consent/terms";
    }

    @GetMapping("/community")
    public String showCommunity(HttpSession session, Model model) {
        ParentConsentDTO consentDTO = (ParentConsentDTO) session.getAttribute("consentStates");
        model.addAttribute("consent", consentDTO);
        return "parent/consent/community";
    }

    @GetMapping("/privacy")
    public String showPrivacy(HttpSession session, Model model) {
        ParentConsentDTO consentDTO = (ParentConsentDTO) session.getAttribute("consentStates");
        model.addAttribute("consent", consentDTO);
        return "parent/consent/privacy";
    }

    // 2단계 동의서 매핑
    @GetMapping("/second")
    public String showSecondConsentForm(HttpSession session, Model model) {
        ParentConsentDTO consentDTO = (ParentConsentDTO) session.getAttribute("consentStates");
        if (consentDTO == null) {
            consentDTO = new ParentConsentDTO();
        }
        model.addAttribute("consent", consentDTO);
        return "parent/consent/consentFormSecond";
    }

    @GetMapping("/medical")
    public String showMedical(HttpSession session, Model model) {
        ParentConsentDTO consentDTO = (ParentConsentDTO) session.getAttribute("consentStates");
        model.addAttribute("consent", consentDTO);
        return "parent/consent/medicalInfo";
    }

    @GetMapping("/emergency")
    public String showEmergency(HttpSession session, Model model) {
        ParentConsentDTO consentDTO = (ParentConsentDTO) session.getAttribute("consentStates");
        model.addAttribute("consent", consentDTO);
        return "parent/consent/emergencyInfo";
    }

    @GetMapping("/photo")
    public String showPhoto(HttpSession session, Model model) {
        ParentConsentDTO consentDTO = (ParentConsentDTO) session.getAttribute("consentStates");
        model.addAttribute("consent", consentDTO);
        return "parent/consent/photo";
    }

    @PostMapping("/submit")
    public String submitConsent(@Valid @ModelAttribute ParentConsentDTO consentDTO,
                                BindingResult bindingResult,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "parent/consent/consentFormSecond";
        }

        try {
            // 세션에서 이전 동의 상태 가져오기
            ParentConsentDTO sessionDTO = (ParentConsentDTO) session.getAttribute("consentStates");
            if (sessionDTO != null) {
                // 1단계 동의 상태 복사
                consentDTO.setTermsConsent(sessionDTO.getTermsConsent());
                consentDTO.setCommunityConsent(sessionDTO.getCommunityConsent());
                consentDTO.setPrivateConsent(sessionDTO.getPrivateConsent());
            }

            parentConsentService.createConsent(consentDTO);
            session.removeAttribute("consentStates");
            return "redirect:/consent/success";
        } catch (Exception e) {
            log.error("동의서 처리 중 에러 발생: ", e);
            redirectAttributes.addFlashAttribute("errorMessage", "처리 중 오류가 발생했습니다.");
            return "redirect:/consent/second";
        }
    }

    @GetMapping("/success")
    public String showSuccessPage() {
        return "parent/consent/success";
    }

    // 동의 상태 검증
    private boolean validateFirstStepConsents(ParentConsentDTO consentDTO) {
        return consentDTO.getTermsConsent() != null && consentDTO.getTermsConsent() &&
                consentDTO.getCommunityConsent() != null && consentDTO.getCommunityConsent() &&
                consentDTO.getPrivateConsent() != null && consentDTO.getPrivateConsent();
    }

}
