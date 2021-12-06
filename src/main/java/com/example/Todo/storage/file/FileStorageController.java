package com.example.Todo.storage.file;

import com.example.Todo.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(path = "api/files")
@RequiredArgsConstructor
public class FileStorageController {
    private final FileStorageService fileStorageService;

    @GetMapping(path = "{fileName}")
    public ResponseEntity<?> download(@PathVariable("fileName") String fileName) throws IOException {
        FileSystemResource resource = fileStorageService.load(fileName);

        return ResponseEntity
                .ok()
                .contentLength(resource.getFile().length())
                .contentType(getMediaType(resource))
                .body(resource);
    }

    private MediaType getMediaType(FileSystemResource resource) {
        return MediaTypeFactory.getMediaType(resource).orElseThrow(
                () -> new BadRequestException("Media type not recognized")
        );
    }
}


