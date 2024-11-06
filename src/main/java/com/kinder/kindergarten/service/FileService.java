package com.kinder.kindergarten.service;

import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class FileService {

  @Value("${uploadPath1}")
  private String uploadPath;

  @Value("${uploadPathMoney1}")
  private String uploadPathMoney;

  public String getUploadPath() {
    return uploadPath;
  }

  public String uploadFile(String uploadPath, String originalFileName, byte[] fileData) throws Exception {
    Ulid ulid = UlidCreator.getUlid();
    String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
    String savedFileName = ulid.toString() + extension;
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

  public String getFullPathMoney(String filename){
    return uploadPathMoney + filename;
  }

  // 디렉토리 생성
  public void createDirectory(String path) {
    File directory = new File(path);
    if (!directory.exists()) {
      directory.mkdirs();
    }
  }

  public String uploadFile_employee(MultipartFile file) {
    if (file.isEmpty()) {
      return null;
    }

    String originalFilename = file.getOriginalFilename();
    String filename = createFileName(originalFilename);
    String fullPath = uploadPath + filename;

    try {
      file.transferTo(new File(fullPath));
    } catch (IOException e) {
      throw new RuntimeException("파일 업로드에 실패했습니다.", e);
    }

    return filename;
  }
  private String createFileName(String originalFilename) {
    String ext = extractExt(originalFilename);
    Ulid ulid = UlidCreator.getUlid();
    String ulid2 = ulid.toString();
    return ulid2 + "." + ext;
  }

  private String extractExt(String originalFilename) {
    int pos = originalFilename.lastIndexOf(".");
    return originalFilename.substring(pos + 1);
  }
}
