package com.kinder.kindergarten.controller.children;

import com.kinder.kindergarten.DTO.children.ChildrenErpDTO;
import com.kinder.kindergarten.DTO.children.ChildrenUpdateDTO;
import com.kinder.kindergarten.service.children.ChildrenService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/erp/children")
@RequiredArgsConstructor
@Log4j2
public class ChildrenController {

    private final ChildrenService childrenService;

    @GetMapping(value = "/register")
    public String erpChildrenRegister(@RequestParam(name = "parentId", required = true) Long parentId, Model model) {
        log.info("GET /erp/children/register with parentId: " + parentId);
        // ERP 시스템에서 원아 등록하는 메서드

        List<ChildrenErpDTO> childrenArray = new ArrayList<>();
        // ChildrenErpDTO를 LIST로 만든다.

        childrenArray.add(new ChildrenErpDTO());
        // 원아 등록하기 에서 자녀 정보 입력 리스트를 1개만 출력한다.

        model.addAttribute("childrenArray", childrenArray);
        model.addAttribute("parentId", parentId);
        // 모델에다가 원아 리스트, 부모id, 반 정보를 담아서 뷰로 전달한다.

        return "children/childrenErpForm";
    }

    @PostMapping("/register")
    public String childrenRegisterPost(@RequestParam Map<String, String> params,
                                       @RequestParam(name = "parentId", required = true) Long parentId,
                                       Model model) {
        log.info("원아 등록할 때 부모아이디 : " + parentId);
        log.info("Received parameters: {}", params);  // 받은 파라미터 출력

        try {
            List<ChildrenErpDTO> childrenList = new ArrayList<>();

            int index = 0;
            while (params.containsKey("childrenArray[" + index + "].childrenName")) {
                ChildrenErpDTO dto = new ChildrenErpDTO();
                dto.setChildrenName(params.get("childrenArray[" + index + "].childrenName"));
                dto.setChildrenBirthDate(LocalDate.parse(params.get("childrenArray[" + index + "].childrenBirthDate")));
                dto.setChildrenGender(params.get("childrenArray[" + index + "].childrenGender"));
                dto.setChildrenBloodType(params.get("childrenArray[" + index + "].childrenBloodType"));
                dto.setChildrenAllergies(params.get("childrenArray[" + index + "].childrenAllergies"));
                dto.setChildrenMedicalHistory(params.get("childrenArray[" + index + "].childrenMedicalHistory"));
                dto.setChildrenNotes(params.get("childrenArray[" + index + "].childrenNotes"));

                log.info("Created DTO for index {}: {}", index, dto);

                if (dto.getChildrenName() != null && !dto.getChildrenName().isEmpty()) {
                    childrenList.add(dto);
                }
                index++;
            }
            log.info("Final childrenList size: {}", childrenList.size());  // 최종 리스트 크기 출력
            List<Long> childrenIds = childrenService.registerChildren(childrenList, parentId);
            log.info("Registered children IDs: {}", childrenIds);  // 등록된 ID 출력

            if (!childrenIds.isEmpty()) {
                return "redirect:/erp/classRoom/assign/" + parentId + "/" + childrenIds.get(0);
            }

            return "redirect:/erp/parent/list";

        } catch (Exception e) {
            log.error("Error registering children: ", e);
            model.addAttribute("errorMessage", "원아 등록 중 오류가 발생했습니다: " + e.getMessage());
            return "children/childrenErpForm";
        }
    }

    @GetMapping("/list")
    public String ChildrenList(@RequestParam(required = false) String searchType, @RequestParam(required = false) String keyword,
                               @RequestParam(defaultValue = "0") int page, Model model) {

        Page<ChildrenErpDTO> childrenPage = childrenService.getChildrenList(searchType, keyword, page);

        model.addAttribute("children", childrenPage);

        return "children/childrenList";
    }

    @GetMapping("/detail/{childrenId}")
    public String childrenDetail(@PathVariable Long childrenId, Model model) {

        ChildrenErpDTO childrenErpDTO = childrenService.getChildrenById(childrenId);

        model.addAttribute("children", childrenErpDTO);

        return "children/childrenDetail";
    }

    @GetMapping("/modify/{childrenId}")
    public String modifyForm(@PathVariable Long childrenId, Model model) {

        try {
            ChildrenErpDTO childrenErpDTO = childrenService.getChildrenById(childrenId);
            ChildrenUpdateDTO updateDTO = new ChildrenUpdateDTO();

            updateDTO.setChildrenId(childrenErpDTO.getChildrenId());
            updateDTO.setChildrenName(childrenErpDTO.getChildrenName());
            updateDTO.setChildrenBirthDate(childrenErpDTO.getChildrenBirthDate());
            updateDTO.setChildrenGender(childrenErpDTO.getChildrenGender().equals("FEMALE") ? "여자" : "남자");
            updateDTO.setChildrenBloodType(childrenErpDTO.getChildrenBloodType());
            updateDTO.setChildrenAllergies(childrenErpDTO.getChildrenAllergies());
            updateDTO.setChildrenMedicalHistory(childrenErpDTO.getChildrenMedicalHistory());
            updateDTO.setChildrenNotes(childrenErpDTO.getChildrenNotes());
            log.info("원아 정보 DTO : " + updateDTO);

            model.addAttribute("children", updateDTO);

            return "children/childrenModifyForm";

        } catch (Exception e) {

            log.error("원아 정보 조회 중 오류 발생 " + e.getMessage());

            return "redirect:/erp/children/list";
        }

    }

    @PostMapping("/modify/{childrenId}")
    public String modifyChildren(@PathVariable Long childrenId,
                                 @ModelAttribute("children") @Valid ChildrenUpdateDTO updateDTO,  // @ModelAttribute 추가
                                 BindingResult bindingResult,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            return "children/childrenModifyForm";
        }

        try {
            childrenService.updateChildren(childrenId, updateDTO);
            return "redirect:/erp/children/detail/" + childrenId;
        } catch (Exception e) {
            log.error("원아 정보 수정 중 오류 발생: ", e);
            model.addAttribute("errorMessage", "원아 정보 수정 중 오류가 발생했습니다.");
            return "children/childrenModifyForm";
        }
    }

    @PostMapping("/delete/{childrenId}")
    public String deleteChildren(@PathVariable Long childrenId, Model model) {
        try {
            childrenService.deleteChildren(childrenId);
            return "redirect:/erp/children/list";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "원아를 찾을 수 없습니다.");
            return "redirect:/erp/children/list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "삭제 중 오류가 발생했습니다.");
            return "redirect:/erp/children/list";
        }
    }
}
