package com.example.video_streaming.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "videos")
public class Video {
    @Id
    private String id;
    private String title;
    private String description;
    private String fileId;  // ID del archivo en GridFS
    private String contentType;
    private long fileSize;
    private LocalDateTime uploadDate;
    
    // Constructor para nuevos videos
    public Video(String title, String description, String fileId, 
                String contentType, long fileSize) {
        this.title = title;
        this.description = description;
        this.fileId = fileId;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.uploadDate = LocalDateTime.now();
    }
}