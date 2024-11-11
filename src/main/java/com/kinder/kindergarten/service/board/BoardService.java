package com.kinder.kindergarten.service.board;


import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;
import com.google.common.cache.LoadingCache;
import com.kinder.kindergarten.DTO.board.BoardDTO;
import com.kinder.kindergarten.DTO.board.BoardFileDTO;
import com.kinder.kindergarten.DTO.board.BoardFormDTO;
import com.kinder.kindergarten.constant.board.BoardType;
import com.kinder.kindergarten.entity.board.BoardEntity;
import com.kinder.kindergarten.entity.board.BoardFileEntity;
import com.kinder.kindergarten.repository.board.BoardFileRepository;
import com.kinder.kindergarten.repository.board.BoardRepository;
import com.kinder.kindergarten.repository.QueryDSL;
import com.kinder.kindergarten.service.FileService;
import com.kinder.kindergarten.util.Hangul;
import com.kinder.kindergarten.util.HtmlSanitizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


        @Service
        @RequiredArgsConstructor
        @Transactional
        @Log4j2
        public class BoardService {

          private final BoardRepository boardRepository;

          private final BoardFileRepository boardFileRepository;

          private final QueryDSL queryDSL;

          private final ModelMapper modelMapper;

          private final FileService fileService;

          private final LoadingCache<String, Page<BoardDTO>> searchCache;

          //application.properties 에 있는 값
          @Value("${uploadPath1}")
          private String uploadPath;

          @Value("${summerImage}")
          private String summerImage;

          public Page<BoardDTO> getBoardsByType(BoardType boardType, Pageable pageable) {
            log.info("페이지 불러오기 - BoardService.getBoardsByType() 실행. boardType: " + boardType + ", pageable 정보: " + pageable);

            // fetch join을 사용하는 메서드 호출
            Page<BoardEntity> boardPage = boardRepository.findByBoardTypeWithFiles(boardType, pageable);

            return boardPage.map(board -> {
                BoardDTO dto = modelMapper.map(board, BoardDTO.class);
                // 이미 fetch join으로 가져온 데이터를 사용
                List<BoardFileDTO> fileList = board.getBoardFiles().stream()
                    .map(file -> modelMapper.map(file, BoardFileDTO.class))
                    .collect(Collectors.toList());
                dto.setBoardFileList(fileList);
                dto.setFileCount(fileList.size());
                return dto;
            });
          }

          @Transactional
          public void saveBoard(BoardFormDTO boardFormDTO) throws Exception{
            // HTML 컨텐츠 sanitize
            boardFormDTO.setBoardContents(HtmlSanitizer.sanitize(boardFormDTO.getBoardContents()));
            Ulid ulid = UlidCreator.getUlid();
            String id = ulid.toString();
            boardFormDTO.setBoardId(id); // UUID대신 사용할 ULID
            BoardEntity board = boardFormDTO.wirteBoard();
            boardRepository.save(board);
            log.info("게시글+파일 저장 - BoardService.saveBoard() 실행" + boardFormDTO);
          }

          public void saveBoardWithFile(BoardFormDTO boardFormDTO, List<MultipartFile> boardFileList) throws Exception{
            log.info("게시글+파일 저장 - BoardService.saveBoardWithFile() 실행" + boardFormDTO);
            // HTML 컨텐츠 sanitize
            boardFormDTO.setBoardContents(HtmlSanitizer.sanitize(boardFormDTO.getBoardContents()));

            Ulid ulid = UlidCreator.getUlid();
            String id = ulid.toString();
            boardFormDTO.setBoardId(id); // UUID대신 사용할 ULID
            BoardEntity board = boardFormDTO.wirteBoard();

            //게시물 정보 먼저 저장
            boardRepository.save(board);

            if (boardFileList != null && !boardFileList.get(0).isEmpty()) {
              boolean isFirstImage = true;

              for (MultipartFile file : boardFileList) {
                BoardFileEntity boardFile = new BoardFileEntity();

                String originalFilename = file.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

                // FileService를 사용하여 파일 저장
                String savedFileName = fileService.uploadFile(uploadPath, originalFilename, file.getBytes());
                String filePath = fileService.getFullPath(savedFileName);

                // BoardFileEntity 설정
                boardFile.setOriginalName(originalFilename);
                boardFile.setModifiedName(savedFileName);
                boardFile.setFilePath(filePath);

                // 새로운 파일 ID 생성
                Ulid fileUlid = UlidCreator.getUlid();
                boardFile.setFileId(fileUlid.toString());

                // boardEntity 설정
                boardFile.setBoardEntity(board);

                //파일 정보 저장
                boardFileRepository.save(boardFile);
              }
            }

            log.info("게시글 저장 - BoardService.saveBoard : "+ boardFormDTO);
          }

          //이미지 파일인지 확인하는 method
          private boolean isImageFile(String extension) {
            return Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".bmp")
                    .contains(extension.toLowerCase());
          }

          //페이지 상세보기 board_id로 찾아서 ModelMapper사용(entity -> DTO 로)
          @Transactional(readOnly = true)
          public BoardDTO getBoard(String board_id) {
            BoardEntity boardEntity = boardRepository.findById(board_id)
                    .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
            
            BoardDTO boardDTO = new BoardDTO();
            boardDTO.setBoardId(boardEntity.getBoardId());
            boardDTO.setBoardTitle(boardEntity.getBoardTitle());
            boardDTO.setBoardContents(boardEntity.getBoardContents());
            boardDTO.setBoardType(boardEntity.getBoardType());
            boardDTO.setBoardWriter(boardEntity.getBoardWriter());
            boardDTO.setViews(boardEntity.getViews());
            boardDTO.setRegiDate(boardEntity.getRegiDate());
            boardDTO.setModiDate(boardEntity.getModiDate());

            // 첨부파일 정보 설정
            List<BoardFileDTO> fileDTOs = boardEntity.getBoardFiles().stream()
                    .map(fileEntity -> {
                        BoardFileDTO fileDTO = new BoardFileDTO();
                        fileDTO.setFileId(fileEntity.getFileId());
                        fileDTO.setOrignalName(fileEntity.getOriginalName());
                        fileDTO.setModifiedName(fileEntity.getModifiedName());
                        fileDTO.setFilePath(fileEntity.getFilePath());
                        fileDTO.setBoardId(board_id);
                        fileDTO.setIsZip(fileEntity.getIsZip());
                        return fileDTO;
                    })
                    .collect(Collectors.toList());

            boardDTO.setBoardFileList(fileDTOs);
            boardDTO.setFileCount(fileDTOs.size());
            
            return boardDTO;
          }

          //첨부파일이 있는경우 불러오기(다운로드용)
          public BoardFileDTO getFile(String fileId) {
            BoardFileEntity fileEntity = boardFileRepository.findById(fileId)
                    .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));

            BoardFileDTO fileDTO = new BoardFileDTO();
            fileDTO.setFileId(fileEntity.getFileId());
            fileDTO.setOrignalName(fileEntity.getOriginalName());
            fileDTO.setFilePath(fileEntity.getFilePath());

            return fileDTO;
          }

          public String uploadSummernoteImage(MultipartFile file) throws Exception {
            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            Ulid ulid = UlidCreator.getUlid(); //ULID 사용

            String savedFileName = ulid.toString() + extension;

            String uploadDir = uploadPath + "/summernote/";
            File uploadPath = new File(uploadDir);

            if (!uploadPath.exists()) {
              uploadPath.mkdirs();
            }

            String savePath = uploadDir + savedFileName;
            file.transferTo(new File(savePath));

            return "/images/summernote/" + savedFileName;
          }

          @Transactional
          public void updateBoard(String boardId, BoardFormDTO boardFormDTO) {
            BoardEntity board = boardRepository.findById(boardId)
                    .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

            board.setBoardTitle(boardFormDTO.getBoardTitle());
            board.setBoardContents(boardFormDTO.getBoardContents());
            board.setBoardType(boardFormDTO.getBoardType());
          }

          @Transactional
          public void updateBoardWithFile(String boardId, BoardFormDTO boardFormDTO,
                                          List<MultipartFile> boardFileList) throws Exception {
            // HTML 컨텐츠 sanitize
            boardFormDTO.setBoardContents(HtmlSanitizer.sanitize(boardFormDTO.getBoardContents()));
            // 기존 게시글 조회
            BoardEntity board = boardRepository.findById(boardId)
                    .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

            // 기존 파일 삭제
            List<BoardFileEntity> existingFiles = boardFileRepository.findByBoardEntity(board);
            for (BoardFileEntity file : existingFiles) {
              fileService.deleteFile(file.getFilePath());
              boardFileRepository.delete(file);
            }

            // 새로운 파일 업로드
            if (boardFileList != null && !boardFileList.get(0).isEmpty()) {
              boolean isFirstImage = true;

              for (MultipartFile file : boardFileList) {
                BoardFileEntity boardFile = new BoardFileEntity();
                String originalFilename = file.getOriginalFilename();

                // FileService를 사용하여 파일 저장
                String savedFileName = fileService.uploadFile(uploadPath, originalFilename, file.getBytes());
                String filePath = fileService.getFullPath(savedFileName);


                // BoardFileEntity 정
                Ulid ulid = UlidCreator.getUlid();
                boardFile.setFileId(ulid.toString());
                boardFile.setOriginalName(originalFilename);
                boardFile.setModifiedName(savedFileName);
                boardFile.setFilePath(filePath);
                boardFile.setBoardEntity(board);

                boardFileRepository.save(boardFile);
              }
            }

            // 게시글 정보 업데이트
            board.setBoardTitle(boardFormDTO.getBoardTitle());
            board.setBoardContents(boardFormDTO.getBoardContents());
            board.setBoardType(boardFormDTO.getBoardType());
          }

          //게시글 삭제
          public void deleteBoard(String boardId){
            boardRepository.deleteByBoardId(boardId);
          }
          //게시글 검색
          public Page<BoardDTO> searchBoards(String keyword, Pageable pageable) {
            try {
              // 캐시 키 생성 (검색어와 페이지 정보 조합)
              String cacheKey = keyword + "_" + pageable.getPageNumber();

              // 캐시에서 결과 조회 시도ㅏ
              return searchCache.get(cacheKey, () -> {
                // 초성 검색인지 확인
                boolean isChosung = keyword.matches("^[ㄱ-ㅎ]+$");

                if (isChosung) {
                  // 초성 검색 로직
                  List<BoardEntity> allBoards = boardRepository.findAll();
                  List<BoardDTO> matchedBoards = allBoards.stream()
                          .filter(entity ->
                                  Hangul.matchesChosung(entity.getBoardTitle(), keyword) ||
                                          Hangul.matchesChosung(entity.getBoardContents(), keyword) ||
                                          Hangul.matchesChosung(entity.getBoardWriter(), keyword))
                          .map(this::convertToDTO)
                          .collect(Collectors.toList());

                  // List를 Page로 변환
                  int start = (int) pageable.getOffset();
                  int end = Math.min((start + pageable.getPageSize()), matchedBoards.size());

                  return new PageImpl<>(
                          matchedBoards.subList(start, end),
                          pageable,
                          matchedBoards.size()
                  );
                } else {
                  // 일반 검색 로직
                  return boardRepository
                          .findByBoardTitleContainingOrBoardContentsContainingOrBoardWriterContaining(
                                  keyword, keyword, keyword, pageable)
                          .map(this::convertToDTO);
                }
              });
            } catch (Exception e) {
              throw new RuntimeException("검색 중 오류가 발생했습니다.", e);
            }
          }

          private BoardDTO convertToDTO(BoardEntity entity) {
            BoardDTO dto = new BoardDTO();
            dto.setBoardId(entity.getBoardId());
            dto.setBoardTitle(entity.getBoardTitle());
            dto.setBoardContents(entity.getBoardContents());
            dto.setBoardType(entity.getBoardType());
            dto.setBoardWriter(entity.getBoardWriter());
            dto.setViews(entity.getViews());
            dto.setRegiDate(entity.getRegiDate());
            dto.setModiDate(entity.getModiDate());
            return dto;
          }

          @Transactional
          public void saveWithZip(BoardFormDTO boardFormDTO, List<MultipartFile> boardFileList) throws Exception {
            log.info("게시글+일반파일+ZIP파일 저장 - BoardService.saveBoardWithBothFiles() 실행" + boardFormDTO);
            // HTML 컨텐츠 sanitize
            boardFormDTO.setBoardContents(HtmlSanitizer.sanitize(boardFormDTO.getBoardContents()));
            // 게시글 ID 생성 및 저장
            Ulid ulid = UlidCreator.getUlid();
            String boardId = ulid.toString();
            boardFormDTO.setBoardId(boardId);
            BoardEntity board = boardFormDTO.wirteBoard();
            boardRepository.save(board);

            if (boardFileList != null && !boardFileList.isEmpty()) {
              // ZIP 파일 생성을 위한 임시 디렉토리
              String tempDir = uploadPath + "/temp/" + boardId + "/";
              File tempFolder = new File(tempDir);
              if (!tempFolder.exists()) {
                tempFolder.mkdirs();
              }

              try {
                // 원본 파일들을 임시 디렉토리에 저장하고 개별 파일 엔티티 생성
                boolean isFirstImage = true;

                for (MultipartFile file : boardFileList) {
                  if (!file.isEmpty()) {
                    BoardFileEntity boardFile = new BoardFileEntity();
                    String originalFilename = file.getOriginalFilename();
                    String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

                    // 개별 파일 저장
                    String savedFileName = fileService.uploadFile(uploadPath, originalFilename, file.getBytes());
                    String filePath = fileService.getFullPath(savedFileName);


                    // 임시 디렉토리에 파일 복사
                    File tempFile = new File(tempDir + originalFilename);
                    file.transferTo(tempFile);

                    // BoardFileEntity 설정
                    Ulid ulid2 = UlidCreator.getUlid();
                    boardFile.setFileId(ulid2.toString());
                    boardFile.setOriginalName(originalFilename);
                    boardFile.setModifiedName(savedFileName);
                    boardFile.setFilePath(filePath);
                    boardFile.setBoardEntity(board);

                    boardFileRepository.save(boardFile);
                  }
                }

                // ZIP 파일 생성
                String zipFileName = "files_" + boardId + ".zip";
                String zipFilePath = uploadPath + "/" + zipFileName;

                // ZIP 파일 생성 및 저장
                FileOutputStream fos = new FileOutputStream(zipFilePath);
                ZipOutputStream zos = new ZipOutputStream(fos);

                File[] files = tempFolder.listFiles();
                byte[] buffer = new byte[1024];

                for (File file : files) {
                  FileInputStream fis = new FileInputStream(file);
                  zos.putNextEntry(new ZipEntry(file.getName()));

                  int length;
                  while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                  }

                  zos.closeEntry();
                  fis.close();
                }

                zos.close();
                fos.close();

                // BoardFileEntity 생성 및 저장 (ZIP 파일)
                BoardFileEntity zipBoardFile = new BoardFileEntity();
                Ulid fileUlid = UlidCreator.getUlid();

                zipBoardFile.setFileId(fileUlid.toString());
                zipBoardFile.setOriginalName(zipFileName);
                zipBoardFile.setModifiedName(zipFileName);
                zipBoardFile.setFilePath(zipFilePath);
                zipBoardFile.setBoardEntity(board);
                zipBoardFile.setIsZip("Y");  // ZIP 파일임을 표시

                boardFileRepository.save(zipBoardFile);

              } finally {
                // 임시 디렉토리 삭제
                deleteDirectory(tempFolder);
              }
            }
          }

          // 디렉토리와 그 내용을 삭제하는 헬퍼 메소드
          private void deleteDirectory(File directory) {
            File[] files = directory.listFiles();
            if (files != null) {
              for (File file : files) {
                if (file.isDirectory()) {
                  deleteDirectory(file);
                } else {
                  file.delete();
                }
              }
            }
            directory.delete();
          }

          // 게시판 타입별 검색 서드 추가
          public Page<BoardDTO> searchBoardsByType(BoardType boardType, String keyword, Pageable pageable) {
              try {
                  // 캐시 키 생성 (게시판 타입, 검색어, 페이지 정보 조합)
                  String cacheKey = boardType + "_" + keyword + "_" + pageable.getPageNumber();

                  // 캐시에서 결과 조회 시도
                  return searchCache.get(cacheKey, () -> {
                      // 초성 검색인지 확인
                      boolean isChosung = keyword.matches("^[ㄱ-ㅎ]+$");

                      if (isChosung) {
                          // 초성 검색 로직
                          List<BoardEntity> allBoards = boardRepository.findByBoardType(boardType);
                          List<BoardDTO> matchedBoards = allBoards.stream()
                                  .filter(entity ->
                                          Hangul.matchesChosung(entity.getBoardTitle(), keyword) ||
                                          Hangul.matchesChosung(entity.getBoardContents(), keyword) ||
                                          Hangul.matchesChosung(entity.getBoardWriter(), keyword))
                                  .map(this::convertToDTO)
                                  .collect(Collectors.toList());

                          // List를 Page로 변환
                          int start = (int) pageable.getOffset();
                          int end = Math.min((start + pageable.getPageSize()), matchedBoards.size());

                          return new PageImpl<>(
                                  matchedBoards.subList(start, end),
                                  pageable,
                                  matchedBoards.size()
                          );
                      } else {
                          // 일반 검색 로직 - 게시판 타입과 검색어를 함께 사용
                          return boardRepository
                                  .findByBoardTypeAndBoardTitleContainingOrBoardTypeAndBoardContentsContainingOrBoardTypeAndBoardWriterContaining(
                                          boardType, keyword, 
                                          boardType, keyword, 
                                          boardType, keyword, 
                                          pageable)
                                  .map(this::convertToDTO);
                      }
                  });
              } catch (Exception e) {
                  throw new RuntimeException("검색 중 오류가 발생했습니다.", e);
              }
          }

          public Page<BoardDTO> getBoardList(int page, String type, String keyword) {
            Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "regiDate"));
            Page<BoardEntity> boardEntities;
            
            if (keyword != null && !keyword.isEmpty()) {
                boardEntities = boardRepository.findByBoardTypeInAndBoardTitleContaining(
                    Arrays.asList(BoardType.valueOf(type), BoardType.ABSOLUTE),
                    keyword, 
                    pageable
                );
            } else {
                boardEntities = boardRepository.findByBoardTypeIn(
                    Arrays.asList(BoardType.valueOf(type), BoardType.ABSOLUTE),
                    pageable
                );
            }
            
            // ABSOLUTE 타입을 상단으로 정렬
            List<BoardDTO> sortedBoards = boardEntities.getContent().stream()
                .sorted((b1, b2) -> {
                    if (b1.getBoardType() == BoardType.ABSOLUTE && b2.getBoardType() != BoardType.ABSOLUTE) {
                        return -1;
                    } else if (b1.getBoardType() != BoardType.ABSOLUTE && b2.getBoardType() == BoardType.ABSOLUTE) {
                        return 1;
                    }
                    return b2.getRegiDate().compareTo(b1.getRegiDate());
                })
                .map(entity -> modelMapper.map(entity, BoardDTO.class))
                .collect(Collectors.toList());

            return new PageImpl<>(sortedBoards, pageable, boardEntities.getTotalElements());
          }
        }