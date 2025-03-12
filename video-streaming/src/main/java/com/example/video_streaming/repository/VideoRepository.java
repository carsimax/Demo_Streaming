package com.example.video_streaming.repository;

import com.example.video_streaming.model.Video;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends MongoRepository<Video, String> {
    // Las operaciones básicas CRUD son proporcionadas automáticamente
}