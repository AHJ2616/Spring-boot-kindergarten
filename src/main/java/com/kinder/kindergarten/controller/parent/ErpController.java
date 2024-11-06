package com.kinder.kindergarten.controller.parent;

import com.kinder.kindergarten.DTO.children.ChildrenErpDTO;
import com.kinder.kindergarten.DTO.parent.ParentErpDTO;
import com.kinder.kindergarten.service.children.ClassRoomService;
import com.kinder.kindergarten.service.parent.ErpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/erp")
@RequiredArgsConstructor
@Log4j2
public class ErpController {

    // ERP - 학부모와 원아에 관한 컨트롤러

    private final ErpService erpService;

    private final ClassRoomService classRoomService;

    @GetMapping(value = "/parent/register")
    public String erpParentRegister(Model model) {
        // ERP 시스템에서 학부모 등록하는 메서드

        model.addAttribute("parentErpDTO", new ParentErpDTO());

        return "parent/parentErpForm";
    }

    @PostMapping(value = "/parent/register")
    public String erpParentRegisterPost(@Valid ParentErpDTO parentErpDTO, BindingResult bindingResult, Model model) {
        // 학부모 등록하기 Post 메서드

        if (bindingResult.hasErrors()) {

            return "parent/parentErpForm";
            // 입력 유효성 검사에서 오류가 발생하면? 폼으로 리턴
        }

        try {
           Long parentId = erpService.parentRegister(parentErpDTO);
            // 서비스에서 parentId 변수에 등록하기 성공 시 자녀 등록 페이지로 이동하면서 학부모 id 전달

            return "redirect:/erp/children/register?parentId=" + parentId;

        } catch (IllegalStateException e) {

            model.addAttribute("errorMessage", e.getMessage());

            return "parent/parentErpForm";

        }

    }

    @GetMapping(value = "/children/register")
    public String erpChildrenRegister(@RequestParam Long parentId, Model model) {
        // ERP 시스템에서 원아 등록하는 메서드

        List<ChildrenErpDTO> childrenList = new ArrayList<>();
        childrenList.add(new ChildrenErpDTO());
        childrenList.add(new ChildrenErpDTO());

        model.addAttribute("childrenList", new ArrayList<ChildrenErpDTO>());
        model.addAttribute("parentId", parentId);
        model.addAttribute("classRooms", classRoomService.getAllClassRooms());
        // 모델에다가 원아 리스트, 부모id, 반 정보를 담아서 뷰로 전달한다.

        return "children/childrenErpForm";
    }

    @PostMapping("/children/register")
    public String erpChildrenRegisterPost(@ModelAttribute("childrenList") List<ChildrenErpDTO> childrenList, @RequestParam Long parentId,
                                          BindingResult bindingResult, Model model) {
        // 원아 등록하기 Post 메서드

        log.info("원아 등록하기 Post : " + childrenList);

        if (bindingResult.hasErrors()) {
            model.addAttribute("classRooms", classRoomService.getAllClassRooms());
            return "children/childrenErpForm";
        }

        try {
            if (childrenList == null) {

                childrenList = new ArrayList<>();
            }

            List<ChildrenErpDTO> validChildren = childrenList.stream()
                    .filter(child -> child != null &&
                            child.getChildrenName() != null &&
                            !child.getChildrenName().isEmpty())
                    .collect(Collectors.toList());
            // 리스트에서 자녀정보를 실제 입력된 자녀 정보로 필터링 한다.

            erpService.childrenRegister(validChildren, parentId);
            // 서비스에다가 원아 등록할때 리스트와 부모id 저장

            return "redirect:/erp/parent/list";

        } catch (Exception e) {

            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("classRooms", classRoomService.getAllClassRooms());
            // 입센션이 발생이 되면 에러메세지와 반 정보를 보낸다.

            return "children/childrenErpForm";
        }
    }
}
