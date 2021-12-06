package com.example.Todo.storage.file;

import com.example.Todo.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService implements StorageService {
    private final FileStorage fileStorage;

    public String save(MultipartFile file) throws IOException {
        Objects.requireNonNull(file);

        String fileName = buildFileName(file);
        File createdFile = createFile(file, fileName);

        return createdFile.getName();
    }

    private String buildFileName(MultipartFile file) {
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());

        return UUID.randomUUID() + "." + extension;
    }

    private File createFile(MultipartFile file, String fileName) throws IOException {
        File createdFile = new File(fileStorage.getSourceAbsolutePath(), fileName);
        OutputStream outputStream = new FileOutputStream(createdFile);
        IOUtils.copy(file.getInputStream(), outputStream);

        return createdFile;
    }

    public FileSystemResource load(String fileName) throws FileNotFoundException {
        Objects.requireNonNull(fileName);

        Path path = Paths.get(fileStorage.getSourceAbsolutePath(), fileName);
        FileSystemResource resource = new FileSystemResource(path);

        if (!resource.exists()) {
            throw new FileNotFoundException("File :" + fileName + " not found");
        }

        return resource;
    }
}
