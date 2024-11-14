package com.kinder.kindergarten.repository.board;

import com.kinder.kindergarten.entity.board.ScheduleEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<ScheduleEntity,String> {
   List<ScheduleEntity> findByType(String type);
}
