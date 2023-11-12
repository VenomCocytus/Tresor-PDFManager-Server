package com.venomclass.filemanager.controller;

import com.venomclass.filemanager.entity.FileDB;
import com.venomclass.filemanager.response.ResponseFile;
import com.venomclass.filemanager.response.ResponseMessage;
import com.venomclass.filemanager.service.FileStorageService;
import com.venomclass.filemanager.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileStorageService storageService;

    @PostMapping("/uploadSingle")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file")MultipartFile file){
        String message = "";
        try {
            storageService.uploadFile(file);
            message = "Successfully uploaded the file: " + file.getOriginalFilename();

            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (Exception e){
            message = "Couldn't upload the file: " + file.getOriginalFilename() + "!";

            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

    @PostMapping("/uploads")
    public ResponseEntity<ResponseMessage> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        String message = "";
        try {
            List<String> uploadedFileNames = new ArrayList<>();
            List<String> existingFileNames = new ArrayList<>();
            List<String> nonPdfFileNames = new ArrayList<>();

            for (MultipartFile file : files) {

                // Check if the uploaded file is a PDF
                if (!file.getContentType().equalsIgnoreCase("application/pdf")) {
                    nonPdfFileNames.add(file.getOriginalFilename());
                    continue; // Skip processing the file and move to the next one
                }

                try{
                    storageService.uploadFile(file);
                    uploadedFileNames.add(file.getOriginalFilename());
                } catch (Exception e){
                        existingFileNames.add(file.getOriginalFilename());
                }
            }

            if (!existingFileNames.isEmpty() && !nonPdfFileNames.isEmpty()) {
                message = "Couldn't upload the files. Existing files with the same names: " + existingFileNames
                        + " and the following files are not in PDF format: " + nonPdfFileNames;
            } else if (!existingFileNames.isEmpty()) {
                message = "Couldn't upload the files. Existing files with the same names: " + existingFileNames;
            } else if (!nonPdfFileNames.isEmpty()) {
                message = "Couldn't upload the files. The following files are not in PDF format: " + nonPdfFileNames;
            } else {
                message = "Successfully uploaded the files: " + uploadedFileNames;
            }

            HttpStatus status = (!existingFileNames.isEmpty() || !nonPdfFileNames.isEmpty()) ?
                    HttpStatus.EXPECTATION_FAILED : HttpStatus.OK;

            return ResponseEntity.status(status).body(new ResponseMessage(message));
        } catch (Exception e) {
            message = "Couldn't upload the files.";

            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

    @GetMapping("/files")
    public ResponseEntity<List<ResponseFile>> getListFiles(){
        List<ResponseFile> files = storageService.getAllFiles().map(dbFile -> {
            String fileDownloadUri = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/files")
                    .path(dbFile.getId())
                    .toUriString();

            return new ResponseFile(
                    dbFile.getFileName(),
                    fileDownloadUri,
                    dbFile.getFileType(),
                    dbFile.getFileData().length,
                    dbFile.getIsArchived());
        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(files);
    }

    @GetMapping("/files/{filename}")
    public ResponseEntity<ResponseFile> getFileByFileName(@PathVariable String filename) {
        try {
            FileDB file = storageService.getFileByName(filename);

            if (file == null) {
                return ResponseEntity.notFound().build();
            }

            String fileDownloadUri = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/files")
                    .path(file.getId())
                    .toUriString();

            ResponseFile responseFile = new ResponseFile(
                    file.getFileName(),
                    fileDownloadUri,
                    file.getFileType(),
                    file.getFileData().length,
                    file.getIsArchived());

            return ResponseEntity.ok().body(responseFile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/files/available")
    public ResponseEntity<List<ResponseFile>> getListAvailableFiles(){
        List<ResponseFile> files = storageService.getAllAvailableFiles().map(dbFile -> {
            String fileDownloadUri = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/files")
                    .path(dbFile.getId())
                    .toUriString();

            return new ResponseFile(
                    dbFile.getFileName(),
                    fileDownloadUri,
                    dbFile.getFileType(),
                    dbFile.getFileData().length,
                    dbFile.getIsArchived());
        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(files);
    }

    @GetMapping("/files/get/{fileName}")
    public ResponseEntity<byte[]> downloadFileByName(@PathVariable String fileName){
        try{
            FileDB fileDB = storageService.getFileByName(fileName);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDB.getFileName() + "\"")
                    .body(FileUtils.decompressFile(fileDB.getFileData()));
        } catch (Exception e){
            return ResponseEntity.notFound().build();
            // return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/files/{filename}/archive")
    public ResponseEntity<String> archiveFileByName(@PathVariable String filename) {
        try {
            FileDB archivedFile = storageService.archiveFile(filename);
            return ResponseEntity.status(HttpStatus.OK).body("File archived successfully: " + archivedFile.getFileName());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Failed to archive file: " + e.getMessage());
        }
    }

    @PutMapping("/files/{filename}/unarchive")
    public ResponseEntity<String> unArchiveFileByName(@PathVariable String filename) {
        try {
            FileDB archivedFile = storageService.unArchiveFile(filename);
            return ResponseEntity.status(HttpStatus.OK).body("File unarchived successfully: " + archivedFile.getFileName());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Failed to unarchive file: " + e.getMessage());
        }
    }

    @PutMapping("/files/{filename}/rename")
    public ResponseEntity<String> renameFileByName(@PathVariable String filename, @RequestParam("newName") String newName) {
        try {
            FileDB renamedFile = storageService.renameFile(filename, newName);
            return ResponseEntity.status(HttpStatus.OK).body("The file, " + filename + " have bee renamed successfully into: " + renamedFile.getFileName());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Failed to rename file: " + e.getMessage());
        }
    }

    @DeleteMapping("/files/{filename}/delete")
    public ResponseEntity<String> deleteFileByName(@PathVariable String filename) {
        try {
            storageService.deleteFileByName(filename);
            return ResponseEntity.status(HttpStatus.OK).body("File deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Failed to delete file: " + e.getMessage());
        }
    }

    @GetMapping("/files/search/{name}")
    public ResponseEntity<List<ResponseFile>> getListAvailableFiles(@PathVariable String name){
        List<ResponseFile> files = storageService.searchFilesByName(name).map(dbFile -> {
            String fileDownloadUri = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/files")
                    .path(dbFile.getId())
                    .toUriString();

            return new ResponseFile(
                    dbFile.getFileName(),
                    fileDownloadUri,
                    dbFile.getFileType(),
                    dbFile.getFileData().length,
                    dbFile.getIsArchived());
        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(files);
    }
}
