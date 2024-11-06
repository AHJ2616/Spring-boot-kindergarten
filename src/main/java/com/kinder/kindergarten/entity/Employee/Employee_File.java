package com.kinder.kindergarten.entity.Employee;

import com.kinder.kindergarten.entity.Employee.Employee;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "Employee_File")
@Entity
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee_File {

    @Id
    @Column(name = "file_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 파일 기본키

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee; // 직원 기본키

    @Column(name = "file_name")
    private String name; // 파일 이름

    @Column(name = "file_original")
    private String original; // 파일 원본이름

    @Column(name = "file_path")
    private String path;
}
