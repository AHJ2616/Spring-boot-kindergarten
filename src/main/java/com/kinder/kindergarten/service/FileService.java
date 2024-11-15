package com.kinder.kindergarten.service;

import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;
import com.kinder.kindergarten.entity.Member;
import com.kinder.kindergarten.entity.employee.Employee;
import com.kinder.kindergarten.entity.employee.Employee_File;
import com.kinder.kindergarten.repository.employee.FileRepository;
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

    @Value("${uploadPath1}")
    private String uploadPath;

    @Value("${uploadPathMoney1}")
    private String uploadPathMoney;

    @Value("${uploadPathMaterial1}")
    private String uploadPathMaterial;

    public String getUploadPath() {
        return uploadPath;
    }

    private final FileRepository fileRepository;

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
        if (deleteFile.exists()) {
            deleteFile.delete();
        }
    }

    public String getFullPath(String filename) {
        return uploadPath + filename;
    }

    public String getFullPathMoney(String filename) {
        return uploadPathMoney + filename;
    }

    public String getFullPathMaterial(String filename) {
        return uploadPathMaterial + filename;
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




    // 이미지 파일 업로드 처리
    public String uploadProfileImage(MultipartFile file, Employee employee) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension =
                    getFileExtension(originalFilename);

            // 이미지 파일 검증
            if (!isImageFile(extension)) {
                throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다. (jpg, jpeg, png)");
            }

            String newFileName = generateFileName() + "." + extension;
            String imagePath = uploadPath + newFileName;

            // 이미지 저장
            file.transferTo(new File(imagePath));

            // 파일 정보 저장
            Employee_File employee_file = Employee_File.builder()
                    .employee(employee)
                    .name(newFileName)
                    .original(originalFilename)
                    .path(imagePath)
                    .build();
            fileRepository.save(employee_file);

            return newFileName;
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 중 오류가 발생했습니다.", e);

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
