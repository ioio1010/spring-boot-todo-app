package com.example.Todo.storage;

import org.springframework.core.io.FileSystemResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {
    String save(MultipartFile file) throws IOException;

    FileSystemResource load(String fileName) throws IOException;
}
