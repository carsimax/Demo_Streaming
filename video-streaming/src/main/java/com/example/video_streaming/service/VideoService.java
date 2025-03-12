package com.example.video_streaming.service;

import com.example.video_streaming.model.Video;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface VideoService {
    Video storeVideo(MultipartFile file, String title, String description) throws IOException;
    Optional<Video> getVideo(String id);
    List<Video> getAllVideos();
    byte[] getVideoContent(String fileId) throws IOException;
    void deleteVideo(String id) throws IOException;
}