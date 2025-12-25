package org.mql.ai.models;

import java.time.LocalDateTime;

public class DocumentMetadata {
    private String id;
    private String fileName;
    private String filePath;
    private long fileSize;
    private int numberOfChunks;
    private boolean indexed;
    private LocalDateTime indexDate;
    
    public DocumentMetadata() {
    }
    
    public DocumentMetadata(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.indexed = false;
    }
        
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
    
    public int getNumberOfChunks() {
        return numberOfChunks;
    }
    
    public void setNumberOfChunks(int numberOfChunks) {
        this.numberOfChunks = numberOfChunks;
    }
    
    public boolean isIndexed() {
        return indexed;
    }
    
    public void setIndexed(boolean indexed) {
        this.indexed = indexed;
    }
    
    public LocalDateTime getIndexDate() {
        return indexDate;
    }
    
    public void setIndexDate(LocalDateTime indexDate) {
        this.indexDate = indexDate;
    }
    
    @Override
    public String toString() {
        return "DocumentMetadata{" +
                "id='" + id + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileSize=" + fileSize +
                ", numberOfChunks=" + numberOfChunks +
                ", indexed=" + indexed +
                ", indexDate=" + indexDate +
                '}';
    }
}