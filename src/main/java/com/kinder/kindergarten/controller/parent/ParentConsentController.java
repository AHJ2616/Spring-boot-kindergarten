package com.kinder.kindergarten.controller.parent;

import com.kinder.kindergarten.DTO.parent.ParentConsentDTO;
import com.kinder.kindergarten.DTO.parent.ParentInfoDTO;
import com.kinder.kindergarten.entity.Member;
import com.kinder.kindergarten.entity.parent.Parent;
import com.kinder.kindergarten.repository.MemberRepository;
import com.kinder.kindergarten.service.parent.ParentConsentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/consent")
@RequiredArgsConstructor
@Log4j2
public class ParentConsentController {

    // 학부모의 동의서에 관한 컨트롤러 ㅇㅇ

    private final ParentConsentService parentConsentService;

    private final MemberRepository memberRepository;

    @GetMapping
    public String showConsentForm(Model model) {
        // 1단계 약관, 동의서 폼 경로 메서드
        // 새로운 빈 DTO 객체를 생성하여 모든 동의 항목을 false로 초기화
        ParentConsentDTO consent = new ParentConsentDTO();
        consent.setTermsConsent(false);
        consent.setCommunityConsent(false);
        consent.setPrivateConsent(false);
        consent.setPhotoConsent(false);
        consent.setMedicalInfoConsent(false);
        consent.setEmergencyInfoConsent(false);

        model.addAttribute("consent", consent);
        //모델에다가 담아서 뷰로 전달한다

        return "parent/consent/consentForm";
    }

    // 1단계 동의서
    @GetMapping("/terms")
    public String showTerms() {
        // ERP 시스템 및 커뮤니티 서비스 이용 약관

        return "parent/consent/terms";
    }

    @GetMapping("/community")
    public String showCommunity() {
        // 학부모 커뮤니티 활동 동의서

        return "parent/consent/community";
    }

    @GetMapping("/privacy")
    public String showPrivacy() {
        // 학부모 개인정보 및 제 3자 제공 동의서

        return "parent/consent/privacy";
    }

    // 2단계 동의서
    @GetMapping("/second")
    public String showSecondConsentForm(@RequestParam(required = false) Long consentId, Model model) {
        // 2단계 약관, 동의서 폼 경로 메서드

        if (consentId != null) {
            ParentConsentDTO consent = parentConsentService.getConsentInfo(consentId);
            consent.setParentConsentId(consentId);
            model.addAttribute("consent", consent);
        } else {
            model.addAttribute("consent", new ParentConsentDTO());
        }
        return "parent/consent/consentFormSecond";
    }

    @PostMapping("/second")
    public String submitSecondStep(@ModelAttribute ParentConsentDTO consentDTO,
                                   RedirectAttributes redirectAttributes) {
        try {
            if (!consentDTO.isSecondStepConsented()) {
                redirectAttributes.addFlashAttribute("error", "모든 항목에 동의해주세요.");
                return "redirect:/consent/second?consentId=" + consentDTO.getParentConsentId();
            }

            parentConsentService.saveSecondStepConsent(consentDTO);
            return "redirect:/consent/parent-info";

        } catch (Exception e) {
            log.error("2단계 동의서 처리 중 오류: ", e);
            redirectAttributes.addFlashAttribute("error", "처리 중 오류가 발생했습니다.");
            return "redirect:/consent/second?consentId=" + consentDTO.getParentConsentId();
        }
    }

    @GetMapping("/medical")
    public String showMedical() {
        // 의료 정보 활용 동의서

        return "parent/consent/medicalInfo";
    }

    @GetMapping("/emergency")
    public String showEmergency() {
        // 비상 연락망 및 응급 상황 동의서

        return "parent/consent/emergencyInfo";
    }

    @GetMapping("/photo")
    public String showPhoto() {
        //사진 및 영상 촬영,활용 동의서

        return "parent/consent/photo";
    }

    @PostMapping("/first")
    public String submitFirstStep(@ModelAttribute ParentConsentDTO consentDTO, RedirectAttributes redirectAttributes) {
        // 1단계 동의서에서 동의 정보 업데이트하는 메서드

        try {
            if (!consentDTO.isFirstStepConsented()) {
                redirectAttributes.addFlashAttribute("error", "모든 항목에 동의해주세요.");
                return "redirect:/consent";
            }

            Long consentId = parentConsentService.saveFirstStepConsent(consentDTO);
            return "redirect:/consent/second?consentId=" + consentId;

        } catch (Exception e) {
            log.error("1단계 동의서 처리 중 오류: ", e);
            redirectAttributes.addFlashAttribute("error", "처리 중 오류가 발생했습니다.");
            return "redirect:/consent";
        }
    }

    @PostMapping("/submit")
    public String submitConsent(@ModelAttribute ParentConsentDTO consentDTO, RedirectAttributes redirectAttributes) {
        // 동의서를 동의함 눌렀을 때 세션으로 넘어가게 하거나 저장하는 메서드

        try {
            parentConsentService.saveSecondStepConsent(consentDTO);

            return "redirect:/consent/parent-info";
        } catch (Exception e) {
            log.error("동의서 처리 중 에러 발생: ", e);
            redirectAttributes.addFlashAttribute("errorMessage", "처리 중 오류가 발생했습니다.");
            return "redirect:/consent/second";
        }
    }

    @GetMapping("/consent-success")
    public String showSuccessPage() {
        // 모든 동의서, 약관을 끝난 성공 페이지

        return "parent/consent/consentSuccess";
    }

    // 동의 상태 검증
    private boolean validateFirstStepConsents(ParentConsentDTO consentDTO) {
        return consentDTO.getTermsConsent() != null && consentDTO.getTermsConsent() &&
                consentDTO.getCommunityConsent() != null && consentDTO.getCommunityConsent() &&
                consentDTO.getPrivateConsent() != null && consentDTO.getPrivateConsent();
    }

    @GetMapping("/parent-info")
    public String showParentInfoForm(Model model) {
        // 3단계에서 학부모의 이메일 및 개인 정보 경로
        if (!model.containsAttribute("parentInfo")) {

            model.addAttribute("parentInfo", new ParentInfoDTO());
        }


        return "parent/consent/parentInfo";
    }

    @PostMapping("/register-parent")
    public String registerParent(@Valid  @ModelAttribute ParentInfoDTO parentInfoDTO,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        log.info("registerParent 호출됨. parentInfoDTO: {}", parentInfoDTO);

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.parentInfo",
                    bindingResult);
            redirectAttributes.addFlashAttribute("parentInfo", parentInfoDTO);
            return "redirect:/consent/parent-info";
        }

        try {
            parentConsentService.saveParentInfo(parentInfoDTO);
            return "redirect:/consent/success";
        } catch (Exception e) {
            log.error("학부모 정보 저장 중 오류: ", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/consent/parent-info";
        }
    }

    @GetMapping("/check-email")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkEmailDuplicate(@RequestParam String email) {
        // 3단계인 학부모 정보에서 이메일 계정이 중복 경로 페이지

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

    @GetMapping("/admin/requests")
    public String listRegistrationRequests(Model model) {
        //  ERP에서 직원이 학부모의 동의서, 약관 내용보고 승인 관리하는 페이지

        List<Parent> pendingRequests = parentConsentService.getPendingRegistrations();
        // 승인 관리하는 페이지에서 리스트 형식으로 나오기 위해 리스트화

        // Member 정보를 Map으로 가져오기
        Map<String, Member> memberMap = pendingRequests.stream()
                .map(Parent::getMemberEmail)
                .distinct()
                .filter(email -> email != null)
                .collect(Collectors.toMap(
                        email -> email,
                        email -> memberRepository.findByEmail(email),
                        (existing, replacement) -> existing
                ));

        log.info("Pending requests: {}", pendingRequests);

        model.addAttribute("requests", pendingRequests);
        model.addAttribute("memberMap", memberMap);

        return "parent/consent/registrationRequests";
    }

    // 승인 처리
    @PostMapping("/admin/approve/{parentId}")
    @ResponseBody
    public ResponseEntity<?> approveRegistration(@PathVariable Long parentId) {
        // ERP에서 직원이 학부모의 동의서, 약관 내용보고 승인 처리 하는 Post 메서드

        try {
            parentConsentService.approveRegistration(parentId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("승인 처리 중 오류 발생: ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 반려 처리
    @PostMapping("/admin/reject/{parentId}")
    @ResponseBody
    public ResponseEntity<?> rejectRegistration( @PathVariable Long parentId, @RequestParam String reason) {
        // ERP에서 직원이 학부모의 동의서, 약관 내용보고 반려 처리를 하는 Post 메서드

        try {
            parentConsentService.rejectRegistration(parentId, reason);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("반려 처리 중 오류 발생: ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 상세 정보 조회
    @GetMapping("/admin/request-details/{parentId}")
    public String showRequestDetails(@PathVariable Long parentId, Model model) {
        // ERP에서 학부모의 동의서, 약관 등 요청한 상세보기 내용을 열어주는 경로

        try {
            Parent parent = parentConsentService.getParentDetails(parentId);
            Member member = memberRepository.findByEmail(parent.getMemberEmail());

            model.addAttribute("parent", parent);
            model.addAttribute("member", member);

            return "parent/consent/requestDetails";  // HTML 페이지 반환
        } catch (Exception e) {
            log.error("상세 정보 조회 중 오류 발생: ", e);
            return "redirect:/consent/admin/requests";
        }
    }

    // 유틸리티 메서드
    private Map<String, Member> getMemberMap(List<Parent> parents) {
        return parents.stream()
                .map(Parent::getMemberEmail)
                .distinct()
                .filter(email -> email != null)
                .collect(Collectors.toMap(
                        email -> email,
                        email -> memberRepository.findByEmail(email),
                        (existing, replacement) -> existing
                ));
    }
}