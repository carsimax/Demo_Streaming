package com.example.video_streaming.service;

import com.example.video_streaming.model.Video;
import com.example.video_streaming.repository.VideoRepository;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class VideoServiceImpl implements VideoService {

    private final VideoRepository videoRepository;
    private final GridFsTemplate gridFsTemplate;

    @Autowired
    public VideoServiceImpl(VideoRepository videoRepository, GridFsTemplate gridFsTemplate) {
        this.videoRepository = videoRepository;
        this.gridFsTemplate = gridFsTemplate;
    }

    @Override
    public Video storeVideo(MultipartFile file, String title, String description) throws IOException {
        // Crear metadatos para GridFS
        DBObject metadata = new BasicDBObject();
        metadata.put("contentType", file.getContentType());
        metadata.put("title", title);
        
        // Almacenar archivo en GridFS
        ObjectId fileId = gridFsTemplate.store(
            file.getInputStream(), 
            file.getOriginalFilename(), 
            file.getContentType(), 
            metadata
        );
        
        // Crear y guardar los metadatos en la colección de videos
        Video video = new Video(
            title,
            description,
            fileId.toString(),  // Convertir ObjectId a String
            file.getContentType(),
            file.getSize()
        );
        
        return videoRepository.save(video);
    }

    @Override
    public Optional<Video> getVideo(String id) {
        return videoRepository.findById(id);
    }

    @Override
    public List<Video> getAllVideos() {
        return videoRepository.findAll();
    }

    @Override
    public byte[] getVideoContent(String fileId) throws IOException {
        // Buscar archivo en GridFS por su ID
        GridFSFile gridFSFile = gridFsTemplate.findOne(
            new Query(Criteria.where("_id").is(new ObjectId(fileId)))
        );
        
        if (gridFSFile == null) {
            throw new IOException("Archivo no encontrado con ID: " + fileId);
        }
        
        // Obtener el recurso y convertirlo a bytes
        GridFsResource resource = gridFsTemplate.getResource(gridFSFile);
        return StreamUtils.copyToByteArray(resource.getInputStream());
    }

    @Override
    public void deleteVideo(String id) throws IOException {
        // Buscar el video por su ID
        Optional<Video> videoOpt = videoRepository.findById(id);
        
        if (videoOpt.isPresent()) {
            Video video = videoOpt.get();
            
            // Eliminar archivo de GridFS
            gridFsTemplate.delete(new Query(Criteria.where("_id").is(new ObjectId(video.getFileId()))));
            
            // Eliminar metadatos
            videoRepository.deleteById(id);
        }
    }
}