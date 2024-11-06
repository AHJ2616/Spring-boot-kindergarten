package com.kinder.kindergarten.controller.children;

import com.kinder.kindergarten.DTO.children.ClassRoomDTO;
import com.kinder.kindergarten.service.children.ClassRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/erp/classRoom")
public class ClassRoomController {

    private final ClassRoomService classRoomService;

    @GetMapping(value = "/register")
    public String classRoomRegister(Model model) {
        // 반 등록하는 겟 메핑 메서드

        model.addAttribute("classRoomDTO", new ClassRoomDTO());
        return "children/classRoomForm";
    }

    @PostMapping("/register")
    public String classRoomRegisterPost(@Valid ClassRoomDTO classRoomDTO, BindingResult bindingResult,
                                    Model model) {
        // 반 등록하는 포스트 메서드

        if (bindingResult.hasErrors()) {
            return "children/classRoomForm";
        }

        try {
            classRoomService.classRoomRegister(classRoomDTO);
            return "redirect:/erp/classRoom/list";

        } catch (IllegalStateException e) {

            model.addAttribute("errorMessage", e.getMessage());

            return "children/classRoomForm";
        }
    }

    @GetMapping("/list")
    public String listClassRooms(Model model) {
        model.addAttribute("classRooms", classRoomService.getAllClassRooms());
        return "children/classRoomList";
    }
}
