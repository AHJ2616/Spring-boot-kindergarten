package com.kinder.kindergarten.controller.children;

import com.kinder.kindergarten.DTO.children.ChildrenErpDTO;
import com.kinder.kindergarten.service.children.ChildrenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
}
