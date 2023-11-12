package com.venomclass.filemanager.repository;

import com.venomclass.filemanager.entity.FileDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileDB, String> {
    Optional<FileDB> findByFileName(String fileName);
    List<FileDB> findByIsArchivedFalse();
    List<FileDB> findByFileNameIsContaining(String filename);
}
