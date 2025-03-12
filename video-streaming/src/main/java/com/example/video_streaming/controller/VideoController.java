package com.example.video_streaming.controller;

import com.example.video_streaming.model.Video;
import com.example.video_streaming.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private final VideoService videoService;

    @Autowired
    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @PostMapping
    public ResponseEntity<Video> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("description") String description) throws IOException {
        
        Video video = videoService.storeVideo(file, title, description);
        return new ResponseEntity<>(video, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Video>> getAllVideos() {
        List<Video> videos = videoService.getAllVideos();
        return ResponseEntity.ok(videos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Video> getVideo(@PathVariable String id) {
        return videoService.getVideo(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/stream/{id}")
    public ResponseEntity<Resource> streamVideo(
        @PathVariable String id,
        @RequestHeader(value = "Range", required = false) String rangeHeader) {
    
    try {
        // Obtener información del video
        Video video = videoService.getVideo(id).orElseThrow(() -> 
            new IOException("Video no encontrado con ID: " + id));
        
        // Obtener contenido completo del video
        byte[] videoContent = videoService.getVideoContent(video.getFileId());
        long contentLength = videoContent.length;
        
        // Preparar headers de respuesta
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(video.getContentType()));
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + video.getTitle() + "\"");
        headers.add("Accept-Ranges", "bytes");
        
        // Estado por defecto - envío de archivo completo
        HttpStatus status = HttpStatus.OK;
        byte[] body = videoContent;
        
        // Manejar solicitudes de rango si existen
        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            status = HttpStatus.PARTIAL_CONTENT;
            
            // Parsear el encabezado de rango (formato: "bytes=inicio-fin")
            String[] ranges = rangeHeader.substring(6).split("-");
            long rangeStart = Long.parseLong(ranges[0]);
            
            // Si no se especifica el fin, usamos el final del archivo
            long rangeEnd;
            if (ranges.length > 1 && !ranges[1].isEmpty()) {
                rangeEnd = Long.parseLong(ranges[1]);
            } else {
                rangeEnd = contentLength - 1;
            }
            
            // Validar el rango solicitado
            if (rangeStart >= contentLength || rangeEnd >= contentLength || rangeStart > rangeEnd) {
                // Rango inválido
                return ResponseEntity
                    .status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
                    .header("Content-Range", "bytes */" + contentLength)
                    .build();
            }
            
            // Calcular la longitud del contenido a enviar
            long rangeLength = rangeEnd - rangeStart + 1;
            
            // Extraer el subconjunto de bytes solicitado
            body = new byte[(int) rangeLength];
            System.arraycopy(videoContent, (int) rangeStart, body, 0, (int) rangeLength);
            
            // Configurar headers específicos para respuesta parcial
            headers.add("Content-Range", String.format("bytes %d-%d/%d", 
                    rangeStart, rangeEnd, contentLength));
            headers.setContentLength(rangeLength);
        } else {
            // Sin rango especificado, enviamos todo el contenido
            headers.setContentLength(contentLength);
        }
        
        // Crear recurso y devolver respuesta
        ByteArrayResource resource = new ByteArrayResource(body);
        return new ResponseEntity<>(resource, headers, status);
        
    } catch (IOException e) {
        return ResponseEntity.notFound().build();
    } catch (NumberFormatException e) {
        // Error al parsear rangos
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVideo(@PathVariable String id) {
        try {
            videoService.deleteVideo(id);
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}