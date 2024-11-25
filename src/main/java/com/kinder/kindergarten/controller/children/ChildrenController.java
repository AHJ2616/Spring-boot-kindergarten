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
    public String childrenRegisterPost(@RequestParam Map<String, String> params, @RequestParam(name = "parentId", required = true) Long parentId, Model model) {
        // ERP 시스템에서 원아 등록 POST 메서드

        log.info("원아 등록할 때 부모아이디 : " + parentId);
        log.info("Received parameters: {}", params);

        try {
            List<ChildrenErpDTO> childrenList = new ArrayList<>();
            //ChildrenErpDTO 에서 원아의 정보를 리스트로 생성한다.

            // 배열 형태로 전달된 자녀 정보를 순차적으로 처리
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
                // 자녀 정보 설정 : 원아 이름, 생년월일, 성별, 혈액형, 알레르기, 병력, 특이사항

                log.info("Created DTO for index {}: {}", index, dto);

                if (dto.getChildrenName() != null && !dto.getChildrenName().isEmpty()) {
                    childrenList.add(dto);
                }// 원아의 이름이 있는 경우에만 리스트에 추가하고 빈 값은 필터링

                index++;
            }
            log.info("Final childrenList size: {}", childrenList.size());  // 최종 리스트 크기 출력
            List<Long> childrenIds = childrenService.registerChildren(childrenList, parentId);
            // 리스트로 만든 원아의 정보를 서비스에서 원아 등록하는 메서드를 호출하고 부모ID와 함께 넘어온다.
            log.info("Registered children IDs: {}", childrenIds);  // 등록된 ID 출력

            if (!childrenIds.isEmpty()) {
                return "redirect:/erp/classRoom/assign/" + parentId + "/" + childrenIds.get(0);
                // 등록된 자녀가 있으면 반 배정 페이지로 리다이렉트
            }

            return "redirect:/erp/parent/list";
            // 등록된 자녀가 없으면 학부모 목록으로 리다이렉트

        } catch (Exception e) {
            log.error("Error registering children: ", e);
            model.addAttribute("errorMessage", "원아 등록 중 오류가 발생했습니다: " + e.getMessage());
            return "children/childrenErpForm";
        }
    }

    @GetMapping("/list")
    public String ChildrenList(@RequestParam(required = false) String searchType, @RequestParam(required = false) String keyword,
                               @RequestParam(defaultValue = "0") int page, Model model) {
        // ERP에서 등록된 모든 원아의 리스트를 보여주는 경로 메서드

        Page<ChildrenErpDTO> childrenPage = childrenService.getChildrenList(searchType, keyword, page);
        // 서비스에서 원아의 리스트 메서드를 호출하면서 검색 창에서 쓰일 키워드 , 타입, 페이지를 불러온다.

        model.addAttribute("children", childrenPage);
        // 그리고 모델에 담아서 뷰로 전달시킨다.

        return "children/childrenList";
    }

    @GetMapping("/detail/{childrenId}")
    public String childrenDetail(@PathVariable Long childrenId, Model model) {
        // 리스트에서 상세 버튼을 누르면 원아의 상세보기 페이지 열어주는 경로 메서드

        ChildrenErpDTO childrenErpDTO = childrenService.getChildrenById(childrenId);
        // 원아의 ID로 서비스에서 호출하고 DTO 꽂는다.

        model.addAttribute("children", childrenErpDTO);
        // 뷰로 ㄱㄱ

        return "children/childrenDetail";
    }

    @GetMapping("/modify/{childrenId}")
    public String modifyForm(@PathVariable Long childrenId, Model model) {
        // 리스트에서 수정 버튼을 누르면 원아의 수정 페이지를 열어주는 경로 메서드

        try {
            ChildrenErpDTO childrenErpDTO = childrenService.getChildrenById(childrenId);
            ChildrenUpdateDTO updateDTO = new ChildrenUpdateDTO();
            // 먼저 원아의 ID를 찾아서 원아의 정보를 불러오고 새로 업데이트 할 집을 생성해준다.

            updateDTO.setChildrenId(childrenErpDTO.getChildrenId());
            updateDTO.setChildrenName(childrenErpDTO.getChildrenName());
            updateDTO.setChildrenBirthDate(childrenErpDTO.getChildrenBirthDate());
            updateDTO.setChildrenGender(childrenErpDTO.getChildrenGender().equals("FEMALE") ? "여자" : "남자");
            updateDTO.setChildrenBloodType(childrenErpDTO.getChildrenBloodType());
            updateDTO.setChildrenAllergies(childrenErpDTO.getChildrenAllergies());
            updateDTO.setChildrenMedicalHistory(childrenErpDTO.getChildrenMedicalHistory());
            updateDTO.setChildrenNotes(childrenErpDTO.getChildrenNotes());
            // 원아의 이름, 생년월일, 성별, 혈액형, 알레르기, 병력정보, 상세사항 등 필드명의 업데이트 사항을 지정해준다.

            log.info("원아 정보 DTO : " + updateDTO);

            model.addAttribute("children", updateDTO);
            // 업데이트 한 데이터를 모델에 담아서 뷰로 전달~

            return "children/childrenModifyForm";

        } catch (Exception e) {

            log.error("원아 정보 조회 중 오류 발생 " + e.getMessage());

            return "redirect:/erp/children/list";
        }

    }

    @PostMapping("/modify/{childrenId}")
    public String modifyChildren(@PathVariable Long childrenId, @ModelAttribute("children") @Valid ChildrenUpdateDTO updateDTO,
                                 BindingResult bindingResult,
                                 Model model) {
        // 원아의 수정 데이터 POST 요청

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
        // 등록된 원아를 삭제하는 POST 요청 메서드

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
