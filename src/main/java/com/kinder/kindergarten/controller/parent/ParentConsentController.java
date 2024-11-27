package com.kinder.kindergarten.controller.parent;

import com.kinder.kindergarten.DTO.parent.ParentConsentDTO;
import com.kinder.kindergarten.DTO.parent.ParentConsentDetailDTO;
import com.kinder.kindergarten.DTO.parent.ParentInfoDTO;
import com.kinder.kindergarten.config.PrincipalDetails;
import com.kinder.kindergarten.entity.Member;
import com.kinder.kindergarten.entity.parent.Parent;
import com.kinder.kindergarten.repository.MemberRepository;
import com.kinder.kindergarten.service.parent.ParentConsentService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            return "redirect:/consent/consent-success";
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
    public String listRegistrationRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
            Page<Parent> pendingRequests = parentConsentService.getPendingRegistrations(pageable);

            // 회원 정보 맵 생성 (마스킹 처리 포함)
            Map<String, Map<String, String>> maskedMemberInfo = new HashMap<>();

            for (Parent parent : pendingRequests.getContent()) {
                Member member = memberRepository.findByEmail(parent.getMemberEmail());
                if (member != null) {
                    Map<String, String> info = new HashMap<>();
                    info.put("name", parentConsentService.maskName(member.getName()));
                    info.put("email", parentConsentService.maskEmail(parent.getMemberEmail()));
                    info.put("phone", parentConsentService.maskPhoneNumber(member.getPhone()));  // 연락처 마스킹
                    maskedMemberInfo.put(parent.getMemberEmail(), info);
                }
            }

            model.addAttribute("requests", pendingRequests);
            model.addAttribute("memberInfo", maskedMemberInfo);
            model.addAttribute("currentPage", page);

            return "parent/consent/registrationRequests";
        } catch (Exception e) {
            log.error("승인 대기 목록 조회 중 오류 발생: ", e);
            return "error/500";
        }
    }

    // 승인 처리
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/admin/approve/{parentId}")
    @ResponseBody
    public ResponseEntity<?> approveRegistration(@PathVariable Long parentId) {
        log.info("승인 요청 받음 - parentId: {}", parentId);

        try {
            parentConsentService.approveRegistration(parentId);
            return ResponseEntity.ok()
                    .body(Map.of("success", true, "message", "승인이 완료되었습니다."));
        } catch (Exception e) {
            log.error("승인 처리 중 오류 발생: ", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 반려 처리
    @PostMapping("/admin/reject/{parentId}")
    @ResponseBody
    public ResponseEntity<?> rejectRegistration(
            @PathVariable Long parentId,
            @RequestParam String reason) {
        log.info("반려 요청 받음 - parentId: {}, reason: {}", parentId, reason);

        try {
            parentConsentService.rejectRegistration(parentId, reason);
            return ResponseEntity.ok()
                    .body(Map.of("success", true, "message", "반려 처리가 완료되었습니다."));
        } catch (Exception e) {
            log.error("반려 처리 중 오류 발생: ", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
}

    // 상세 정보 조회
    @GetMapping("/admin/request-details/{parentId}")
    public String showRequestDetails(@PathVariable Long parentId, Model model) {
        // ERP에서 학부모의 동의서, 약관 등 요청한 상세보기 내용을 열어주는 경로

        try {
            Parent parent = parentConsentService.getParentDetails(parentId);
            Member member = memberRepository.findByEmail(parent.getMemberEmail());

            // DTO 생성 및 마스킹 처리
            ParentConsentDetailDTO dto = ParentConsentDetailDTO.from(parent, member);

            // 마스킹 처리
            dto.setName(parentConsentService.maskName(dto.getName()));
            dto.setEmail(parentConsentService.maskEmail(dto.getEmail()));
            dto.setPhone(parentConsentService.maskPhoneNumber(dto.getPhone()));
            dto.setChildrenEmergencyPhone(parentConsentService.maskPhoneNumber(dto.getChildrenEmergencyPhone()));
            dto.setAddress(parentConsentService.maskAddress(dto.getAddress()));
            dto.setDetailAddress("***");

            model.addAttribute("detail", dto);
            model.addAttribute("parent", parent);  // 원본 Parent 객체도 함께 전달

            return "parent/consent/requestDetails";
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

    @GetMapping("/admin/check-auth")
    @ResponseBody
    public ResponseEntity<?> checkAuth(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
        }

        return ResponseEntity.ok()
                .body(Map.of(
                        "isAuthenticated", true,
                        "role", principalDetails.getEmployee().getRole(),
                        "email", principalDetails.getUsername()
                ));
    }
}