package com.stackoverflowcompetitor.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class MediaServiceTest {

    private MediaService mediaService;
    private String uploadDir;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        mediaService = new MediaService();

        // Use reflection to access the private UPLOAD_DIR field
        Field uploadDirField = MediaService.class.getDeclaredField("UPLOAD_DIR");
        uploadDirField.setAccessible(true);
        uploadDir = (String) uploadDirField.get(mediaService);

        // Clean up and create the upload directory
        FileSystemUtils.deleteRecursively(new File(uploadDir));
        new File(uploadDir).mkdirs();
    }

    @Test
    void testUploadFile_Success() throws IOException {
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Hello, World!".getBytes()
        );

        String filePath = mediaService.uploadFile(file);

        assertNotNull(filePath);
        assertTrue(Files.exists(Paths.get(filePath)));
    }

    @Test
    void testUploadFile_EmptyFile() {
        MultipartFile file = new MockMultipartFile(
                "file",
                "empty.txt",
                "text/plain",
                new byte[0]
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            mediaService.uploadFile(file);
        });

        assertEquals("File is empty", exception.getMessage());
    }
}
