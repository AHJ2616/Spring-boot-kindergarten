package com.kinder.kindergarten.entity.board;

import com.kinder.kindergarten.entity.TimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

@Entity
@Table(name = "board_file")
@Getter
@Setter
@ToString
@Transactional
@Log4j2
public class BoardFileEntity extends TimeEntity {

  @Id
  @Column(name = "file_id")
  private String fileId;

  @Column(nullable = false)
  private String originalName;

  @Column(nullable = false)
  private String modifiedName;

  @Column(nullable = false)
  private String filePath;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="board_id", referencedColumnName = "board_id")
  private BoardEntity boardEntity;

  @Column(name = "is_zip")
  private String isZip = "N";

  @PreRemove
  private void deleteFile() {
    try {
      File file = new File(this.filePath + "/" + this.modifiedName);
      if (file.exists()) {
        file.delete();
      }
      
      // ZIP 파일이 있는 경우 ZIP 파일도 삭제
      if ("Y".equals(this.isZip)) {
        File zipFile = new File(this.filePath + "/" + this.modifiedName.substring(0, this.modifiedName.lastIndexOf(".")) + ".zip");
        if (zipFile.exists()) {
          zipFile.delete();
        }
      }
    } catch (Exception e) {
      
      log.info(e);
    }
  }

}
