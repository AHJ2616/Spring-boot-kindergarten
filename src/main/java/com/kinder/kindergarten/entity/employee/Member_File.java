package com.kinder.kindergarten.entity.employee;

import com.kinder.kindergarten.entity.Member;
import com.kinder.kindergarten.entity.TimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "member_file")
@Entity
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member_File extends TimeEntity {

    @Id
    @Column(name = "file_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "file_name", nullable = false)
    private String name;

    @Column(name = "file_original",nullable = false)
    private String original;

    @Column(name = "file_path", nullable = false)
    private String path;
}
