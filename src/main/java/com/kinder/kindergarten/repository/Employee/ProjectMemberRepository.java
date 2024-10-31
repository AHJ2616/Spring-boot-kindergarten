package com.kinder.kindergarten.repository.Employee;

import com.kinder.kindergarten.entity.Employee.Employee;
import com.kinder.kindergarten.entity.Employee.Project;
import com.kinder.kindergarten.entity.Employee.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    List<ProjectMember> findByProject(Project project);
    List<ProjectMember> findByEmployee(Employee employee);
    List<ProjectMember> findByProjectAndIsActiveTrue(Project project);
}
