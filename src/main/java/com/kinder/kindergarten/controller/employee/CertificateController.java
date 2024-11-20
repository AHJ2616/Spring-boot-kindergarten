package com.kinder.kindergarten.controller.employee;

import com.kinder.kindergarten.DTO.employee.CertificateDTO;
import com.kinder.kindergarten.config.PrincipalDetails;
import com.kinder.kindergarten.service.employee.CertificateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/certificate")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    @GetMapping("/upload")
    public String leaveRequestForm() {
        return "/employee/certificate_reg";
    }

    @PostMapping("/upload")
    public String uploadCertificate(@Valid CertificateDTO certificateDTO,
                                    @RequestParam("file") MultipartFile file,
                                    @AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {

        try {
            certificateService.saveCertificate(certificateDTO, file, principalDetails.getMember());
            return "redirect:/certificate/certificate_list";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "/employee/certificate_reg";
        }
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<Resource> downloadCertificate(@PathVariable Long id) {
        CertificateDTO certificate = certificateService.getCertificateById(id);
        try {
            Path filePath = Paths.get( "D:/upload/"+certificate.getCe_path());
            Resource resource = new UrlResource(filePath.toUri());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + certificate.getCe_name() + ".pdf\"")
                    .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                    .body(resource);
        } catch (MalformedURLException e) {
            throw new RuntimeException("파일을 찾을 수 없습니다.", e);
        }
    }

    @GetMapping("/certificate_list")
    public String getMyCertificates(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                    Model model) {
        List<CertificateDTO> certificates = certificateService.getCertificatesByEmployee(principalDetails.getMember());
        model.addAttribute("certificates", certificates);
        return "/employee/certificate_list";
    }

    @GetMapping("/edit/{id}")
    public String editCertificateForm(@PathVariable Long id,
                                      @AuthenticationPrincipal PrincipalDetails principalDetails,
                                      Model model) {
        if (!certificateService.isCertificateOwnedByEmployee(id, principalDetails.getMember())) {
            return "redirect:/employee/certificate_list";
        }

        CertificateDTO certificate = certificateService.getCertificateById(id);
        model.addAttribute("certificateDTO", certificate);
        return "/employee/certificate_edit";
    }

    @PostMapping("/edit/{id}")
    public String updateCertificate(@PathVariable Long id,
                                    @Valid CertificateDTO certificateDTO,
                                    @RequestParam(value = "file", required = false) MultipartFile file,
                                    @AuthenticationPrincipal PrincipalDetails principalDetails,
                                    Model model) {
        try {
            certificateService.updateCertificate(id, certificateDTO, file, principalDetails.getMember());
            return "redirect:/certificate/certificate_list";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("certificateDTO", certificateDTO);
            return "/employee/certificate_edit";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteCertificate(@PathVariable Long id,
                                    @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (certificateService.isCertificateOwnedByEmployee(id, principalDetails.getMember())) {
            certificateService.deleteCertificate(id, principalDetails.getMember());
        }
        return "redirect:/certificate/certificate_list";
    }
}

