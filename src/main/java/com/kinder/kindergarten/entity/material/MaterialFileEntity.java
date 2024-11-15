package com.kinder.kindergarten.entity.material;

import com.kinder.kindergarten.entity.TimeEntity;
import com.kinder.kindergarten.entity.money.MoneyEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="material_File")
@Getter
@Setter
public class MaterialFileEntity extends TimeEntity {

    @Id
    @Column(name = "file_id")
    private String fileId;

    @Column(nullable = false)
    private String originalName;

    @Column(nullable = false)
    private String modifiedName;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private String mainFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="materialId", referencedColumnName = "materialId")
    private MaterialEntity materialEntity;



}
