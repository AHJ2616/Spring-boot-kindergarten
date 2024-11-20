package com.kinder.kindergarten.controller.employee;

import com.kinder.kindergarten.DTO.employee.EducationDTO;
import com.kinder.kindergarten.config.PrincipalDetails;
import com.kinder.kindergarten.service.employee.EducationService;
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
@RequestMapping("/education")
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;

    @GetMapping("/record")
    public String leaveRequestForm() {
        return "/employee/education_reg";
    }

    @PostMapping("/record")
    public String recordEducation(@Valid EducationDTO educationDTO,
                                  @RequestParam(value = "file", required = false) MultipartFile file,
                                  @AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
        try {
            educationService.saveEducation(educationDTO, file, principalDetails.getMember());
            return "redirect:/education/education_list";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "/employee/education_reg";
        }
    }

    @GetMapping("/education_list")
    public String getEducationHistory(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                      Model model) {
        List<EducationDTO> history = educationService.getEducationHistory(principalDetails.getMember());
        model.addAttribute("history", history);
        return "/employee/education_list";
    }

    @GetMapping("/view/{id}")
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

    @GetMapping("/edit/{id}")
    public String editEducationForm(@PathVariable Long id,
                                    @AuthenticationPrincipal PrincipalDetails principalDetails,
                                    Model model) {
        if (!educationService.isEducationOwnedByEmployee(id, principalDetails.getMember())) {
            return "redirect:/education/education_list";
        }

        EducationDTO education = educationService.getEducationById(id);
        model.addAttribute("educationDTO", education);
        return "/employee/education_edit";
    }

    @PostMapping("/edit/{id}")
    public String updateEducation(@PathVariable Long id,
                                  @Valid EducationDTO educationDTO,
                                  @RequestParam(value = "file", required = false) MultipartFile file,
                                  @AuthenticationPrincipal PrincipalDetails principalDetails,
                                  Model model) {
        try {
            educationService.updateEducation(id, educationDTO, file, principalDetails.getMember());
            return "redirect:/education/education_list";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("educationDTO", educationDTO);
            return "/employee/education_edit";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteEducation(@PathVariable Long id,
                                  @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (educationService.isEducationOwnedByEmployee(id, principalDetails.getMember())) {
            educationService.deleteEducation(id, principalDetails.getMember());
        }
        return "redirect:/education/education_list";
    }

}
