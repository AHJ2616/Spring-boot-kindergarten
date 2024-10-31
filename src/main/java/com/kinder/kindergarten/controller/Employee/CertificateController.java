package com.kinder.kindergarten.controller.Employee;

import com.kinder.kindergarten.config.PrincipalDetails;
import com.kinder.kindergarten.dto.Employee.CertificateDTO;
import com.kinder.kindergarten.service.Employee.CertificateService;
import com.kinder.kindergarten.service.Employee.FileService;
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

    @PostMapping("/upload")
    public String uploadCertificate(@RequestParam("file") MultipartFile file,
                                    @Valid CertificateDTO certificateDTO,
                                    @AuthenticationPrincipal PrincipalDetails principalDetails) {
        String filePath = fileService.uploadFile(file);
        certificateService.saveCertificate(certificateDTO, filePath, principalDetails.getEmployee());
        return "redirect:/certificate/my-certificates";
    }

    @GetMapping("/my-certificates")
    public String getMyCertificates(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                    Model model) {
        List<CertificateDTO> certificates = certificateService.getCertificatesByEmployee(principalDetails.getEmployee());
        model.addAttribute("certificates", certificates);
        return "certificate/list";
    }
}
