package com.example.Todo.storage.file;

import lombok.Getter;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;

@Component
public class FileStorage {
    public final String SOURCE_PATH = "src/main/resources/uploads";

    @Getter
    private final String sourceAbsolutePath;

    public FileStorage() throws FileNotFoundException {
        this.sourceAbsolutePath = findSourceAbsolutePath();
    }

    private String findSourceAbsolutePath() throws FileNotFoundException {
        FileSystemResource resource = new FileSystemResource(SOURCE_PATH);

        if (!resource.exists()) {
            throw new FileNotFoundException("Uploads folder not found, expected folder: " + SOURCE_PATH);
        }

        return resource.getFile().getAbsolutePath();
    }
}
