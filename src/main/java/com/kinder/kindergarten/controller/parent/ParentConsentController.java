package com.kinder.kindergarten.controller.parent;

import com.kinder.kindergarten.DTO.parent.ParentConsentDTO;
import com.kinder.kindergarten.DTO.parent.ParentInfoDTO;
import com.kinder.kindergarten.service.parent.ParentConsentService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.catalina.Session;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

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
            session.setAttribute("consentStates", consentDTO);
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
            return "redirect:/consent"; // 첫 단계부터 시작하도록 리다이렉트
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

    @PostMapping("/first")
    public String submitFirstStep(@ModelAttribute ParentConsentDTO consentDTO,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        try {
            // 세션에 첫 번째 단계 동의 정보 저장
            ParentConsentDTO sessionDTO = (ParentConsentDTO) session.getAttribute("consentStates");
            if (sessionDTO == null) {
                sessionDTO = new ParentConsentDTO();
            }

            // 첫 번째 단계 동의 정보 업데이트
            sessionDTO.setTermsConsent(consentDTO.getTermsConsent());
            sessionDTO.setCommunityConsent(consentDTO.getCommunityConsent());
            sessionDTO.setPrivateConsent(consentDTO.getPrivateConsent());

            session.setAttribute("consentStates", sessionDTO);

            return "redirect:/consent/second";
        } catch (Exception e) {
            log.error("동의서 처리 중 에러 발생: ", e);
            redirectAttributes.addFlashAttribute("errorMessage", "처리 중 오류가 발생했습니다.");
            return "redirect:/consent";
        }
    }

    @PostMapping("/submit")
    public String submitConsent(@ModelAttribute ParentConsentDTO consentDTO,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        try {
            // 세션에서 이전 동의 상태 가져오기
            ParentConsentDTO sessionDTO = (ParentConsentDTO) session.getAttribute("consentStates");
            if (sessionDTO != null) {
                // 1단계 동의 상태 복사
                consentDTO.setTermsConsent(sessionDTO.getTermsConsent());
                consentDTO.setCommunityConsent(sessionDTO.getCommunityConsent());
                consentDTO.setPrivateConsent(sessionDTO.getPrivateConsent());
            }

            // 임시 저장
            parentConsentService.saveTemporaryConsent(consentDTO);
            session.setAttribute("finalConsent", consentDTO);
            session.removeAttribute("consentStates");

            // parentInfo.html로 리다이렉트
            return "redirect:/consent/parent-info";
        } catch (Exception e) {
            log.error("동의서 처리 중 에러 발생: ", e);
            redirectAttributes.addFlashAttribute("errorMessage", "처리 중 오류가 발생했습니다.");
            return "redirect:/consent/second";
        }
    }

    @GetMapping("/consent-success")  // URL 매핑도 수정
    public String showSuccessPage() {
        return "parent/consent/consentSuccess";  // 뷰 경로도 수정
    }

    // 동의 상태 검증
    private boolean validateFirstStepConsents(ParentConsentDTO consentDTO) {
        return consentDTO.getTermsConsent() != null && consentDTO.getTermsConsent() &&
                consentDTO.getCommunityConsent() != null && consentDTO.getCommunityConsent() &&
                consentDTO.getPrivateConsent() != null && consentDTO.getPrivateConsent();
    }

    @GetMapping("/parent-info")
    public String showParentInfoForm(HttpSession session, Model model) {
        // 동의 정보 확인
        ParentConsentDTO consentDTO = (ParentConsentDTO) session.getAttribute("finalConsent");
        if (consentDTO == null) {
            // 동의 정보가 없으면 동의 페이지로 리다이렉트
            return "redirect:/consent";
        }

        // ParentInfoDTO 객체 생성
        ParentInfoDTO parentInfoDTO = new ParentInfoDTO();
        model.addAttribute("parentInfo", parentInfoDTO);

        return "parent/consent/parentInfo";
    }

    @PostMapping("/register-parent")
    public String registerParent(@Valid @ModelAttribute("parentInfo") ParentInfoDTO parentInfoDTO,
                                 BindingResult bindingResult,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "parent/consent/parentInfo";
        }

        try {
            // 동의 정보 가져오기
            ParentConsentDTO consentDTO = (ParentConsentDTO) session.getAttribute("finalConsent");
            if (consentDTO == null) {
                throw new IllegalStateException("동의 정보가 없습니다.");
            }

            // 학부모 정보와 동의 정보 함께 저장
            parentConsentService.saveParentInfoAndConsent(parentInfoDTO, consentDTO);

            // 세션의 동의 정보 제거
            session.removeAttribute("finalConsent");

            return "redirect:/consent/consent-success";
        } catch (Exception e) {
            log.error("학부모 등록 중 에러 발생: ", e);
            redirectAttributes.addFlashAttribute("errorMessage", "등록 중 오류가 발생했습니다.");
            return "redirect:/consent/parent-info";
        }
    }

    @GetMapping("/check-email")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkEmailDuplicate(@RequestParam String email) {

        Map<String, Object> response = new HashMap<>();

        try {
            boolean isDuplicate = parentConsentService.isEmailDuplicate(email);

            response.put("isDuplicate", isDuplicate);
            response.put("message", isDuplicate ? "이미 사용 중인 이메일입니다." : "사용 가능한 이메일입니다.");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "이메일 중복 확인 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}