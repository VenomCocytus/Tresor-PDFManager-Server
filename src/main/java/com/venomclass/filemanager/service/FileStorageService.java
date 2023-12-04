package com.venomclass.filemanager.service;

import com.venomclass.filemanager.entity.FileDB;
import com.venomclass.filemanager.repository.FileRepository;
import com.venomclass.filemanager.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class FileStorageService {

    @Autowired
    private FileRepository repo;

    public FileDB uploadFile(MultipartFile file) throws Exception{
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try{
            if(fileName.contains("..")){
                throw new Exception("Filename contains invalid path sequence: " + fileName);
            }

//            if (!file.getContentType().equalsIgnoreCase("application/pdf")) {
//                throw new Exception("The file submitted is not in PDF format: " + fileName);
//            }

            FileDB fileObj =  new FileDB(fileName, file.getContentType(), FileUtils.compressFile(file.getBytes()));

            return repo.save(fileObj);
        } catch (Exception e){
            throw new Exception("Couldn't not save File: " + fileName);
        }
    }

    public FileDB getFileByName(String name) throws Exception {
        Optional<FileDB> fileOptional = repo.findByFileName(name);
        if (fileOptional.isPresent()) {
            return fileOptional.get();
        } else {
            throw new Exception("File not found with name: " + name);
        }
    }

    public Stream<FileDB> getAllFiles(){
        return repo.findAll().stream();
    }

    public Stream<FileDB> getAllAvailableFiles() {
        return repo.findByIsArchivedFalse().stream();
    }

    // Delete method
    public void deleteFileByName(String filename) throws Exception {
        Optional<FileDB> fileOptional = repo.findByFileName(filename);
        if (fileOptional.isPresent()) {
            FileDB file = fileOptional.get();
            repo.delete(file);
        } else {
            throw new Exception("File not found with name: " + filename);
        }
    }

    // Search method
    public Stream<FileDB> searchFilesByName(String name) {
        return repo.findByFileNameIsContaining(name).stream();
    }

    public FileDB renameFile(String filename, String newName) throws Exception {
        Optional<FileDB> fileOptional = repo.findByFileName(filename);
        if (fileOptional.isPresent()) {
            FileDB file = fileOptional.get();
            String cleanNewName = StringUtils.cleanPath(newName);
            if (cleanNewName.contains("..")) {
                throw new Exception("New filename contains invalid path sequence: " + newName);
            }
            file.setFileName(cleanNewName);
            return repo.save(file);
        } else {
            throw new Exception("File not found with filename: " + filename);
        }
    }

    // Archive a file
    public FileDB archiveFile(String filename) throws Exception {
        Optional<FileDB> fileOptional = repo.findByFileName(filename);
        if (fileOptional.isPresent()) {
            FileDB file = fileOptional.get();
            file.setIsArchived(true);
            return repo.save(file);
        } else {
            throw new Exception("File not found with filename: " + filename);
        }
    }

    public FileDB unArchiveFile(String filename) throws Exception {
        Optional<FileDB> fileOptional = repo.findByFileName(filename);
        if (fileOptional.isPresent()) {
            FileDB file = fileOptional.get();
            file.setIsArchived(false);
            return repo.save(file);
        } else {
            throw new Exception("File not found with filename: " + filename);
        }
    }

    public FileDB updateFileInfo(String filename, String newFileName, Boolean state) throws Exception {
        Optional<FileDB> fileOptional = repo.findByFileName(filename);
        if (fileOptional.isPresent()) {
            FileDB file = fileOptional.get();

            if (newFileName.contains("..")) {
                throw new Exception("New filename contains invalid path sequence: " + newFileName);
            }

            file.setFileName(newFileName);
            file.setIsArchived(state);

            return repo.save(file);
        } else {
            throw new Exception("File not found with filename: " + filename);
        }
    }
}
