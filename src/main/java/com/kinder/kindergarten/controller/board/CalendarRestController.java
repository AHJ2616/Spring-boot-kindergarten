package com.kinder.kindergarten.controller.board;

import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;
import com.kinder.kindergarten.DTO.board.ScheduleDTO;
import com.kinder.kindergarten.service.board.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
public class CalendarRestController {

  private final ScheduleService scheduleService;

  @GetMapping("/events")
  public List<ScheduleDTO> getEvents() {

    List<ScheduleDTO> events = scheduleService.getAllSchedules();
    log.info("캘린더 불러오기");
    for(ScheduleDTO item : events){
      log.info(item);
    }

    return events;
  }

  @PostMapping("/events/add")
  public ResponseEntity<ScheduleDTO> createEvent(@RequestBody ScheduleDTO scheduleDTO) {
    log.info("캘린더 저장하기");
    try {
      Ulid ulid = UlidCreator.getUlid();
      scheduleDTO.setId(ulid.toString());
      ScheduleDTO savedSchedule = scheduleService.createSchedule(scheduleDTO);
      return ResponseEntity.ok(savedSchedule);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
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

  @DeleteMapping("/events/delete/{id}")
  public ResponseEntity<Void> deleteEvent(@PathVariable String id) {
    try {
      scheduleService.deleteSchedule(id);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }
}
