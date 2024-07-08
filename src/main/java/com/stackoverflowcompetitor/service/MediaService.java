package com.stackoverflowcompetitor.service;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class MediaService {

    // this can be read from resource repository
    private static final String UPLOAD_DIR = "src/main/resources/static/media/";

    public String uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String originalFilename = file.getOriginalFilename();
        String newFilename = UUID.randomUUID().toString() + "_" + originalFilename;
        Files.copy(file.getInputStream(), Paths.get(UPLOAD_DIR + newFilename));
        return UPLOAD_DIR + newFilename;
    }
}

