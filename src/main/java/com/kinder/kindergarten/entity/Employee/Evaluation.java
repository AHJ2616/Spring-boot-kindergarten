package com.kinder.kindergarten.entity.Employee;

import com.kinder.kindergarten.entity.Employee.Employee;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "evaluation")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Evaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private LocalDate evaluationDate;
    private String evaluator;
    private String content;
    private Integer score;
}
