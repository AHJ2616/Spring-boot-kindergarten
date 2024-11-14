package com.kinder.kindergarten.controller.board;

import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;
import com.kinder.kindergarten.DTO.board.ScheduleDTO;
import com.kinder.kindergarten.config.PrincipalDetails;
import com.kinder.kindergarten.service.board.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
public class CalendarRestController {

  private final ScheduleService scheduleService;

  @GetMapping("/events")
  public ResponseEntity<List<ScheduleDTO>> getEvents() {
    try {
      List<ScheduleDTO> events = scheduleService.getAllSchedules();
      events.forEach(event -> {
        if (event.getStart() == null || event.getEnd() == null) {
          throw new RuntimeException("Invalid event data: start or end date is null");
        }
        if (!event.getStart().contains("T")) {
          event.setStart(event.getStart() + "T00:00:00");
        }
        if (!event.getEnd().contains("T")) {
          event.setEnd(event.getEnd() + "T23:59:59");
        }
        if (event.getTextColor() == null) {
          event.setTextColor("#ffffff");
        }
      });
      return ResponseEntity.ok(events);
    } catch (Exception e) {
      log.error("이벤트 조회 중 오류 발생", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  @PostMapping("/events/add")
  public ResponseEntity<ScheduleDTO> createEvent(@RequestBody ScheduleDTO scheduleDTO,@AuthenticationPrincipal PrincipalDetails principal) {
    try {
      log.info("일정 저장 요청 데이터: {}", scheduleDTO);
      
      // 필수 필드 검증
      if (scheduleDTO.getTitle() == null || scheduleDTO.getTitle().trim().isEmpty()) {
        return ResponseEntity.badRequest().build();
      }
      
      // ID 생성
      Ulid ulid = UlidCreator.getUlid();
      scheduleDTO.setId(ulid.toString());
      scheduleDTO.setUsername(principal.getName());
      
      // 기본값 설정
      if (scheduleDTO.getDescription() == null) {
        scheduleDTO.setDescription("");
      }
      if (scheduleDTO.getTextColor() == null) {
        scheduleDTO.setTextColor("#ffffff");
      }
      
      ScheduleDTO savedSchedule = scheduleService.createSchedule(scheduleDTO);
      log.info("저장된 일정: {}", savedSchedule);
      
      return ResponseEntity.ok(savedSchedule);
    } catch (Exception e) {
      log.error("일정 저장 중 오류 발생", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PutMapping("/events/{id}")
  public ResponseEntity<ScheduleDTO> updateEvent(@PathVariable String id, @RequestBody ScheduleDTO scheduleDTO) {
    try {
      scheduleDTO.setId(id);
      ScheduleDTO updatedSchedule = scheduleService.updateSchedule(id, scheduleDTO);
      return ResponseEntity.ok(updatedSchedule);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @PutMapping("/events/drag/{id}")
  public ResponseEntity<ScheduleDTO> dragUpdateEvent(@PathVariable String id, @RequestBody ScheduleDTO scheduleDTO) {
    try {
      scheduleDTO.setId(id);
      ScheduleDTO updatedSchedule = scheduleService.dragUpdateSchedule(id, scheduleDTO);
      return ResponseEntity.ok(updatedSchedule);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @DeleteMapping("/events/delete/{id}")
  public ResponseEntity<Void> deleteEvent(@PathVariable String id) {
    try {
      scheduleService.deleteSchedule(id);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @GetMapping("/api/schedules/type/{type}")
  public ResponseEntity<List<ScheduleDTO>> getSchedulesByType(@PathVariable String type) {
    List<ScheduleDTO> schedules = scheduleService.findSchedulesByType(type);
    return ResponseEntity.ok(schedules);
  }
}
