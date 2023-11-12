package com.venomclass.filemanager.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseFile {
    private String fileName;
    private String downloadUrl;
    private String fileType;
    private long fileSize;
    private boolean fileIsArchived;

    public ResponseFile(String fileName, String downloadUrl, String fileType, long fileSize, Boolean fileIsArchived) {
        this.fileName = fileName;
        this.downloadUrl = downloadUrl;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.fileIsArchived = fileIsArchived;
    }
}
