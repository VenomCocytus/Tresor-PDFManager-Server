package com.venomclasses.tresorseen.entity;

//import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;

//@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "files")
public class FileDB {
    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

//    @Column(nullable = false, length = 255, unique = true)
    private String fileName;

//    @Column(nullable = false, length = 255)
    private String fileType;

//    @Column(nullable = false)
    private Boolean isArchived;

//    @Lob
//    @Column(name = "file_data", columnDefinition = "LONGBLOB")
    private byte[] fileData;

//    @Column(name = "created_at")
    private Timestamp createdAt;

//    @Column(name = "updated_at")
    private Timestamp updatedAt;

//    @Column(name = "created_by")
    private String createdBy;

//    @Column(name = "update_by")
    private String updateBy;

//    @PrePersist
//    private void setCreatedAt() {
//        this.createdAt = new Timestamp(System.currentTimeMillis());
//    }
//
//    @PreUpdate
//    private void setUpdatedAt() {
//        this.updatedAt = new Timestamp(System.currentTimeMillis());
//    }

    public FileDB(String fileName, String fileType, byte[] fileData){
//                  Timestamp createAt, Timestamp updatedAt,
//                  String createdBy, String updateBy){
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileData = fileData;
        this.isArchived = false;
//        this.createdAt = createAt;
//        this.updatedAt = updatedAt;
//        this.createdBy = createdBy;
//        this.updateBy = updateBy;
    }
}

