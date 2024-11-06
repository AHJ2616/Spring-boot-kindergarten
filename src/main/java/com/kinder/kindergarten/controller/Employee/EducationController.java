package com.kinder.kindergarten.controller.Employee;

import com.kinder.kindergarten.config.PrincipalDetails;
import com.kinder.kindergarten.DTO.Employee.EducationDTO;
import com.kinder.kindergarten.service.Employee.EducationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
@RequestMapping("/education")
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;

    @GetMapping("/record")
    public String leaveRequestForm() {
        return "employee/record";
    }

    @PostMapping("/record")
    public String recordEducation(@Valid EducationDTO educationDTO,
                                  @RequestParam(value = "file", required = false) MultipartFile file,
                                  @AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
        try {
            educationService.saveEducation(educationDTO, file, principalDetails.getEmployee());
            return "redirect:/education/history";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "employee/record";
        }
    }

    @GetMapping("/history")
    public String getEducationHistory(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                      Model model) {
        List<EducationDTO> history = educationService.getEducationHistory(principalDetails.getEmployee());
        model.addAttribute("history", history);
        return "employee/history";
    }

    @GetMapping("/preview/{id}")
    @ResponseBody
    public ResponseEntity<Resource> previewPdf(@PathVariable Long id) {
        try {
            EducationDTO education = educationService.getEducationById(id);
            Path path = Paths.get(education.getEd_path());
            Resource resource = new UrlResource(path.toUri());

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadCertificate(@PathVariable Long id) {
        EducationDTO education = educationService.getEducationById(id);
        try {
            Path filePath = Paths.get( "D:/upload/"+education.getEd_path());
            Resource resource = new UrlResource(filePath.toUri());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + education.getEd_name() + ".pdf\"")
                    .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                    .body(resource);
        } catch (MalformedURLException e) {
            throw new RuntimeException("파일을 찾을 수 없습니다.", e);
        }
    }

}
