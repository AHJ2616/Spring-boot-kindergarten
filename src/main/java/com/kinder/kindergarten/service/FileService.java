package com.kinder.kindergarten.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

  @Value("${uploadPath1}")
  private String uploadPath;

  public String getUploadPath() {
    return uploadPath;
  }

  public String uploadFile(String uploadPath, String originalFileName, byte[] fileData) throws Exception {
    UUID uuid = UUID.randomUUID();
    String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
    String savedFileName = uuid.toString() + extension;
    String fileUploadFullUrl = uploadPath + savedFileName;

    // 파일 저장
    Files.write(Paths.get(fileUploadFullUrl), fileData);

    return savedFileName;
  }

  public void deleteFile(String filePath) {
    File deleteFile = new File(filePath);
    if(deleteFile.exists()) {
      deleteFile.delete();
    }
  }

  public String getFullPath(String filename) {
    return uploadPath + filename;
  }

  // 디렉토리 생성
  public void createDirectory(String path) {
    File directory = new File(path);
    if (!directory.exists()) {
      directory.mkdirs();
    }
  }
}
