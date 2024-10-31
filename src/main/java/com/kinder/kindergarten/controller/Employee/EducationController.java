package com.kinder.kindergarten.controller.Employee;

import com.kinder.kindergarten.config.PrincipalDetails;
import com.kinder.kindergarten.dto.Employee.EducationDTO;
import com.kinder.kindergarten.service.Employee.EducationService;
import com.kinder.kindergarten.service.Employee.FileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/education")
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;
    private final FileService fileService;

    @PostMapping("/record")
    public String recordEducation(@Valid EducationDTO educationDTO,
                                  @RequestParam(value = "file", required = false) MultipartFile file,
                                  @AuthenticationPrincipal PrincipalDetails principalDetails) {
        String certificatePath = null;
        if (file != null && !file.isEmpty()) {
            certificatePath = fileService.uploadFile(file);
        }
        educationService.saveEducation(educationDTO, certificatePath, principalDetails.getEmployee());
        return "redirect:/education/history";
    }

    @GetMapping("/history")
    public String getEducationHistory(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                      Model model) {
        List<EducationDTO> history = educationService.getEducationHistory(principalDetails.getEmployee());
        model.addAttribute("history", history);
        return "education/history";
    }
}
