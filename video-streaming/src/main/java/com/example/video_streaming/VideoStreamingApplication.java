package com.example.video_streaming;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.PostConstruct;
import java.util.Set;

@SpringBootApplication
public class VideoStreamingApplication {

    private final MongoTemplate mongoTemplate;
    private static final Logger logger = LoggerFactory.getLogger(VideoStreamingApplication.class);

    @Autowired
    public VideoStreamingApplication(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @PostConstruct
    public void checkDatabaseConnection() {
        try {
            // Verificar conexión a MongoDB Atlas
            Set<String> collectionNames = mongoTemplate.getCollectionNames();
            logger.info("Conexión a MongoDB Atlas exitosa. Colecciones disponibles: {}", collectionNames);
        } catch (Exception e) {
            logger.error("No se pudo conectar a MongoDB Atlas. Error: {}", e.getMessage());
            logger.error("Detalles de la excepción:", e);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(VideoStreamingApplication.class, args);
    }
}