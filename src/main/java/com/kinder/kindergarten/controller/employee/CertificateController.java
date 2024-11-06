package com.kinder.kindergarten.controller.employee;

import com.kinder.kindergarten.config.PrincipalDetails;
import com.kinder.kindergarten.DTO.employee.CertificateDTO;
import com.kinder.kindergarten.service.employee.CertificateService;
import com.kinder.kindergarten.service.FileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/certificate")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;
    private final FileService fileService;

    @GetMapping("/upload")
    public String leaveRequestForm() {
        return "employee/upload";
    }

    @PostMapping("/upload")
    public String uploadCertificate(@RequestParam("file") MultipartFile file,
                                    @Valid CertificateDTO certificateDTO,
                                    @AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {

        try {
        certificateService.saveCertificate(certificateDTO, file, principalDetails.getEmployee());
        return "redirect:/certificate/list";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "employee/upload";
        }
    }

    @GetMapping("/list")
    public String getMyCertificates(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                    Model model) {
        List<CertificateDTO> certificates = certificateService.getCertificatesByEmployee(principalDetails.getEmployee());
        model.addAttribute("certificates", certificates);
        return "employee/clist";
    }
}
