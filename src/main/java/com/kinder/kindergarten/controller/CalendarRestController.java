package com.kinder.kindergarten.controller;

import com.kinder.kindergarten.DTO.board.ScheduleDTO;
import com.kinder.kindergarten.service.board.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CalendarRestController {

  private final ScheduleService scheduleService;

  @GetMapping("/events")
  public ResponseEntity<List<ScheduleDTO>> getEvents() {
    return ResponseEntity.ok(scheduleService.getAllSchedules());
  }

  @PostMapping("/events/add")
  public ResponseEntity<ScheduleDTO> createEvent(@RequestBody ScheduleDTO scheduleDTO) {
    try {
      ScheduleDTO savedSchedule = scheduleService.createSchedule(scheduleDTO);
      return ResponseEntity.ok(savedSchedule);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @PutMapping("/events/{id}")
  public ResponseEntity<ScheduleDTO> updateEvent(@PathVariable String id, @RequestBody ScheduleDTO scheduleDTO) {
    try {
      scheduleDTO.setScheduleId(id);
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
