package com.kinder.kindergarten.controller.children;

import com.kinder.kindergarten.DTO.children.ClassRoomDTO;
import com.kinder.kindergarten.entity.parent.Parent;
import com.kinder.kindergarten.repository.parent.ParentRepository;
import com.kinder.kindergarten.service.children.ClassRoomService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/erp/classRoom")
@Log4j2
public class ClassRoomController {

    private final ClassRoomService classRoomService;

    private final ParentRepository parentRepository;

    // 반 개설하기
    @GetMapping(value = "/register")
    public String classRoomRegister(Model model) {
        // 반 개설하는 경로

        model.addAttribute("classRoomDTO", new ClassRoomDTO());
        // 반 정보가 담긴 classRoomDTO를 모델에 담아서 뷰로 전달한다.

        return "children/classRoomForm";
    }

    @PostMapping("/register")
    public String classRoomRegisterPost(@Valid ClassRoomDTO classRoomDTO, BindingResult bindingResult,
                                    Model model) {
        // 반 개설하는 포스트 처리 메서드

        if (bindingResult.hasErrors()) {
            // 유효성 검사를 하고 오류가 나오면 리턴.

            return "children/classRoomForm";
        }

        try {
            classRoomService.createClassRoom(classRoomDTO);
            // 서비스에서 반 개설하기 메서드를 불어온다.

            return "redirect:/erp/classRoom/list";
            // 정상적이면 반 목록 리스트로 이동한다.

        } catch (IllegalStateException e) {

            model.addAttribute("errorMessage", e.getMessage());
            // 오류가 나오면 에러 메세지 표출

            return "children/classRoomForm";
        }
    }

    @GetMapping("/list")
    public String listClassRooms(Model model) {
        // 개설된 전체 반 목록 조회

        model.addAttribute("classRooms", classRoomService.getAllClassRooms());
        // 서비스에서 개설된 반 목록 조회하는 메서드를 호출해서 모델에 담아 뷰로 전달

        return "children/classRoomList";
    }

    @GetMapping("/assign/{parentId}/{childrenId}")
    public String showAssignmentPage(@PathVariable Long parentId, @PathVariable Long childrenId, Model model) {
        // ERP 원아 반 배정할 때 경로

        List<ClassRoomDTO> availableClasses = classRoomService.getAvailableClassRooms();
        // 원아가 반 배정이 가능한 반을 조회한다.

        model.addAttribute("parentId", parentId);
        model.addAttribute("childrenId", childrenId);
        model.addAttribute("classRooms", availableClasses);
        // 부모ID, 원아ID, 반 정보를 모델에 담아서 뷰로 전달

        return "children/childrenClassRoom";
    }

    @PostMapping("/assign")
    public String assignClass(@RequestParam Long parentId, @RequestParam Long childrenId, @RequestParam Long classRoomId,
                              RedirectAttributes redirectAttributes) {
        // 원아 반 배정을 처리하는 Post

        try {
            classRoomService.assignChildToClassRoom(childrenId, classRoomId);

            Long nextUnassignedChildId = classRoomService.findNextUnassignedChild(parentId);

            if (nextUnassignedChildId != null) {
                return "redirect:/erp/classRoom/assign/" + parentId + "/" + nextUnassignedChildId;
            }

            // URL 파라미터로 전달
            return "redirect:/erp/parent/registration/completion?parentId=" + parentId
                    + "&childrenId=" + childrenId
                    + "&classRoomId=" + classRoomId;

        } catch (Exception e) {
            log.error("반 배정 중 오류 발생: ", e);
            redirectAttributes.addFlashAttribute("errorMessage", "반 배정 중 오류가 발생했습니다.");
            return "redirect:/erp/classRoom/assign/" + parentId + "/" + childrenId;
        }
    }
}
