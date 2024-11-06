package com.kinder.kindergarten.service;

import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;
import com.kinder.kindergarten.entity.Employee.Employee;
import com.kinder.kindergarten.entity.Employee.Employee_File;
import com.kinder.kindergarten.repository.Employee.FileRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class FileService {

  private final FileRepository fileRepository;

  @Value("${uploadPath1}")
  private String uploadPath;

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

  // 디렉토리 생성
  public void createDirectory(String path) {
    File directory = new File(path);
    if (!directory.exists()) {
      directory.mkdirs();
    }
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

  public String uploadAndConvertToPdf(MultipartFile file, Employee employee) {
    try {
      String originalFilename = file.getOriginalFilename();
      String extension = getFileExtension(originalFilename);
      String newFileName = generateFileName();

      // PDF 파일이면 바로 저장
      if ("pdf".equalsIgnoreCase(extension)) {
        String pdfPath = uploadPath + newFileName + ".pdf";

        Employee_File employee_file = Employee_File.builder()
                .employee(employee)
                .name(newFileName + ".pdf")
                .original(originalFilename)
                .path(pdfPath)
                .build();
        fileRepository.save(employee_file);

        file.transferTo(new File(pdfPath));
        return newFileName + ".pdf";
      }
      // 이미지 파일이면 PDF로 변환
      if (isImageFile(extension)) {
        return convertImageToPdf(file, newFileName, employee);
      }
      // 지원하지 않는 파일 형식
      throw new IllegalArgumentException("지원하지 않는 파일 형식입니다. (지원 형식: PDF, JPG, PNG, JPEG)");
    } catch (Exception e) {
      throw new RuntimeException("파일 처리 중 오류가 발생했습니다.", e);
    }
  }
  private String convertImageToPdf(MultipartFile file, String newFileName, Employee employee) throws IOException {
    PDDocument document = new PDDocument();
    try {
      PDPage page = new PDPage();
      document.addPage(page);
      PDImageXObject image = PDImageXObject.createFromByteArray(document,
              file.getBytes(), file.getOriginalFilename());

      PDPageContentStream contentStream = new PDPageContentStream(document, page);
      // 이미지 크기 조정
      float scale = Math.min(
              page.getMediaBox().getWidth() / image.getWidth(),
              page.getMediaBox().getHeight() / image.getHeight()
      );

      contentStream.drawImage(image, 0, 0,
              image.getWidth() * scale,
              image.getHeight() * scale);
      contentStream.close();
      String pdfPath = uploadPath + newFileName + ".pdf";
      document.save(pdfPath);

      Employee_File employee_file = Employee_File.builder()
              .employee(employee)
              .name(newFileName + ".pdf")
              .original(file.getOriginalFilename())
              .path(pdfPath)
              .build();
      fileRepository.save(employee_file);

      return newFileName + ".pdf";
    } finally {
      document.close();
    }
  }

  private boolean isImageFile(String extension) {
    return Arrays.asList("jpg", "jpeg", "png").contains(extension.toLowerCase());
  }

  private String getFileExtension(String filename) {
    return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
  }

  private String generateFileName() {
    return UlidCreator.getUlid().toString();
  }
}
