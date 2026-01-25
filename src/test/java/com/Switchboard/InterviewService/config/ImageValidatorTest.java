package com.Switchboard.InterviewService.config;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ImageValidatorTest {

    private ImageValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new ImageValidator();
    }

    @Test
    void isValid_WithNullFile_ShouldReturnTrue() {
        // Arrange
        MultipartFile file = null;

        // Act
        boolean result = validator.isValid(file, context);

        // Assert
        assertTrue(result);
    }

    @Test
    void isValid_WithEmptyFile_ShouldReturnTrue() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile("image", "", "image/jpeg", new byte[0]);

        // Act
        boolean result = validator.isValid(file, context);

        // Assert
        assertTrue(result);
    }

    @Test
    void isValid_WithJpegFile_ShouldReturnTrue() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test content".getBytes()
        );

        // Act
        boolean result = validator.isValid(file, context);

        // Assert
        assertTrue(result);
    }

    @Test
    void isValid_WithJpgFile_ShouldReturnTrue() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpg",
                "test content".getBytes()
        );

        // Act
        boolean result = validator.isValid(file, context);

        // Assert
        assertTrue(result);
    }

    @Test
    void isValid_WithPngFile_ShouldReturnTrue() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test.png",
                "image/png",
                "test content".getBytes()
        );

        // Act
        boolean result = validator.isValid(file, context);

        // Assert
        assertTrue(result);
    }

    @Test
    void isValid_WithGifFile_ShouldReturnTrue() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test.gif",
                "image/gif",
                "test content".getBytes()
        );

        // Act
        boolean result = validator.isValid(file, context);

        // Assert
        assertTrue(result);
    }

    @Test
    void isValid_WithWebpFile_ShouldReturnTrue() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test.webp",
                "image/webp",
                "test content".getBytes()
        );

        // Act
        boolean result = validator.isValid(file, context);

        // Assert
        assertTrue(result);
    }

    @Test
    void isValid_WithUppercaseContentType_ShouldReturnTrue() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test.jpg",
                "IMAGE/JPEG",
                "test content".getBytes()
        );

        // Act
        boolean result = validator.isValid(file, context);

        // Assert
        assertTrue(result);
    }

    @Test
    void isValid_WithInvalidContentType_ShouldReturnFalse() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "test content".getBytes()
        );

        // Act
        boolean result = validator.isValid(file, context);

        // Assert
        assertFalse(result);
    }

    @Test
    void isValid_WithTextFile_ShouldReturnFalse() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "test content".getBytes()
        );

        // Act
        boolean result = validator.isValid(file, context);

        // Assert
        assertFalse(result);
    }

    @Test
    void isValid_WithVideoFile_ShouldReturnFalse() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.mp4",
                "video/mp4",
                "test content".getBytes()
        );

        // Act
        boolean result = validator.isValid(file, context);

        // Assert
        assertFalse(result);
    }

    @Test
    void isValid_WithNullContentType_ShouldReturnFalse() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.unknown",
                null,
                "test content".getBytes()
        );

        // Act
        boolean result = validator.isValid(file, context);

        // Assert
        assertFalse(result);
    }
}
