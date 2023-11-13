package com.venomclasses.tresorseen.repository;

import com.venomclasses.tresorseen.entity.FileDB;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends MongoRepository<FileDB, String> {
    Optional<FileDB> findByFileName(String fileName);
    List<FileDB> findByIsArchivedFalse();
    List<FileDB> findByFileNameIsContaining(String filename);
}

