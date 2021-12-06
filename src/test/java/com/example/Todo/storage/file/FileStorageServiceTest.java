package com.example.Todo.storage.file;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class FileStorageServiceTest {
    public static final String SOURCE_PATH = "src/test/resources/uploads";

    @Mock
    private FileStorage fileStorage;

    @InjectMocks
    private FileStorageService fileStorageService;

    @AfterEach
    public void clearUploads() throws IOException {
        FileUtils.cleanDirectory(new File(SOURCE_PATH));
    }

    private MockMultipartFile getMockMultipartJPEGFile() {
        return new MockMultipartFile(
                "file",
                "file.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "Image bytes".getBytes()
        );
    }

    @Nested
    class Save {
        @Test
        void givenValidMultipartFileWhenSaveThenFileExistInFilesystem() throws IOException {
            MockMultipartFile mockFile = getMockMultipartJPEGFile();

            when(fileStorage.getSourceAbsolutePath()).thenReturn(SOURCE_PATH);

            String actualFileName = fileStorageService.save(mockFile);
            File actualFile = new File(SOURCE_PATH, actualFileName);

            assertThat(actualFile.exists()).isTrue();

            verify(fileStorage).getSourceAbsolutePath();
        }

        @Test
        void givenValidMultipartFileWhenSaveThenReturnFileName() throws IOException {
            MockMultipartFile mockFile = getMockMultipartJPEGFile();

            when(fileStorage.getSourceAbsolutePath()).thenReturn(SOURCE_PATH);

            String actualFileName = fileStorageService.save(mockFile);

            assertThat(actualFileName).isNotNull();

            verify(fileStorage).getSourceAbsolutePath();
        }
    }

    @Nested
    class Load {
        @Test
        void givenExistedFileNameWhenLoadThenReturnFileSystemResource() throws IOException {
            MockMultipartFile mockFile = getMockMultipartJPEGFile();

            when(fileStorage.getSourceAbsolutePath()).thenReturn(SOURCE_PATH);

            String actualFileName = fileStorageService.save(mockFile);
            FileSystemResource actualResource = fileStorageService.load(actualFileName);

            assertThat(actualResource).isNotNull();

            verify(fileStorage, times(2)).getSourceAbsolutePath();
        }

        @Test
        void givenNotExistedFileNameWhenLoadThenReturnFileNotFoundException() {
            String givenFileName = "fake.jpg";

            when(fileStorage.getSourceAbsolutePath()).thenReturn(SOURCE_PATH);

            assertThatExceptionOfType(FileNotFoundException.class)
                    .isThrownBy(
                            () -> fileStorageService.load(givenFileName)
                    ).withMessage("File :" + givenFileName + " not found");

            verify(fileStorage).getSourceAbsolutePath();
        }
    }
}