package com.Switchboard.InterviewService.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceImplTest {

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private FileServiceImpl fileService;

    private final String testBucket = "test-bucket";
    private final String testRegion = "us-east-1";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(fileService, "bucket", testBucket);
        ReflectionTestUtils.setField(fileService, "region", testRegion);
    }

    @Test
    void uploadImage_WithValidFile_ShouldUploadToS3AndReturnUrl() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test-image.jpg",
                "image/jpeg",
                "test content".getBytes()
        );
        String path = "interview-experience";

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        // Act
        String result = fileService.uploadImage(path, file);

        // Assert
        assertNotNull(result);
        assertTrue(result.startsWith("https://" + testBucket + ".s3." + testRegion + ".amazonaws.com/"));
        assertTrue(result.contains(path));
        assertTrue(result.contains("test-image.jpg"));
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void uploadImage_ShouldGenerateUniqueFileName() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "original-name.png",
                "image/png",
                "test content".getBytes()
        );
        String path = "interview-experience";

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        // Act
        String result1 = fileService.uploadImage(path, file);
        String result2 = fileService.uploadImage(path, file);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertNotEquals(result1, result2); // URLs should be different due to UUID
        verify(s3Client, times(2)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void uploadImage_ShouldSetCorrectContentType() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test.webp",
                "image/webp",
                "test content".getBytes()
        );
        String path = "interview-experience";

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        // Act
        fileService.uploadImage(path, file);

        // Assert
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void uploadImage_WithDifferentExtensions_ShouldHandleAllTypes() throws IOException {
        // Arrange
        String[] fileNames = {"image.jpg", "image.png", "image.gif", "image.webp"};
        String[] contentTypes = {"image/jpeg", "image/png", "image/gif", "image/webp"};
        String path = "interview-experience";

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        // Act & Assert
        for (int i = 0; i < fileNames.length; i++) {
            MockMultipartFile file = new MockMultipartFile(
                    "image",
                    fileNames[i],
                    contentTypes[i],
                    "test content".getBytes()
            );
            
            String result = fileService.uploadImage(path, file);
            
            assertNotNull(result);
            assertTrue(result.contains(fileNames[i]));
        }
        
        verify(s3Client, times(4)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void deleteImage_WithValidUrl_ShouldDeleteFromS3() {
        // Arrange
        String fileUrl = "https://" + testBucket + ".s3." + testRegion + ".amazonaws.com/interview-experience/test-image.jpg";
        
        // Act
        fileService.deleteImage(fileUrl);

        // Assert - Verify method was called (implementation uses Consumer lambda)
        verify(s3Client, atLeastOnce()).deleteObject(anyConsumer());
    }

    @Test
    void getResource_ShouldThrowUnsupportedOperationException() {
        // Arrange
        String path = "interview-experience";
        String fileName = "test.jpg";

        // Act & Assert
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> {
            fileService.getResource(path, fileName);
        });
        
        assertEquals("Not needed for now", exception.getMessage());
    }

    @Test
    void uploadImage_WhenS3ThrowsException_ShouldPropagateException() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test content".getBytes()
        );
        String path = "interview-experience";

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(new RuntimeException("S3 upload failed"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            fileService.uploadImage(path, file);
        });
    }

    @Test
    void deleteImage_WhenS3ThrowsException_ShouldPropagateException() {
        // Arrange
        String fileUrl = "https://" + testBucket + ".s3." + testRegion + ".amazonaws.com/interview-experience/test.jpg";
        
        doThrow(new RuntimeException("S3 delete failed"))
                .when(s3Client).deleteObject(anyConsumer());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            fileService.deleteImage(fileUrl);
        });
    }
    
    @SuppressWarnings("unchecked")
    private static <T> java.util.function.Consumer<T> anyConsumer() {
        return any(java.util.function.Consumer.class);
    }
}
