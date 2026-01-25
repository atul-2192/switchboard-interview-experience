package com.Switchboard.InterviewService.dto;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

class InterviewExperienceRequestTest {

    @Test
    void builder_ShouldCreateRequest() {
        // Arrange
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test".getBytes());

        // Act
        InterviewExperienceRequest request = InterviewExperienceRequest.builder()
                .userName("John Doe")
                .userEmail("john.doe@example.com")
                .title("My Interview Experience")
                .content("This is the content of my interview.")
                .companyTag("Google")
                .image(image)
                .build();

        // Assert
        assertNotNull(request);
        assertEquals("John Doe", request.getUserName());
        assertEquals("john.doe@example.com", request.getUserEmail());
        assertEquals("My Interview Experience", request.getTitle());
        assertEquals("This is the content of my interview.", request.getContent());
        assertEquals("Google", request.getCompanyTag());
        assertEquals(image, request.getImage());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Arrange
        InterviewExperienceRequest request = new InterviewExperienceRequest();
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test".getBytes());

        // Act
        request.setUserName("Jane Smith");
        request.setUserEmail("jane.smith@example.com");
        request.setTitle("Backend Interview");
        request.setContent("Detailed interview content.");
        request.setCompanyTag("Amazon");
        request.setImage(image);

        // Assert
        assertEquals("Jane Smith", request.getUserName());
        assertEquals("jane.smith@example.com", request.getUserEmail());
        assertEquals("Backend Interview", request.getTitle());
        assertEquals("Detailed interview content.", request.getContent());
        assertEquals("Amazon", request.getCompanyTag());
        assertEquals(image, request.getImage());
    }

    @Test
    void noArgsConstructor_ShouldCreateEmptyRequest() {
        // Act
        InterviewExperienceRequest request = new InterviewExperienceRequest();

        // Assert
        assertNotNull(request);
        assertNull(request.getUserName());
        assertNull(request.getUserEmail());
        assertNull(request.getTitle());
        assertNull(request.getContent());
        assertNull(request.getCompanyTag());
        assertNull(request.getImage());
    }

    @Test
    void allArgsConstructor_ShouldCreateFullRequest() {
        // Arrange
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test".getBytes());

        // Act
        InterviewExperienceRequest request = new InterviewExperienceRequest(
                "John Doe",
                "john@example.com",
                "My Title",
                "My Content",
                "Google",
                image
        );

        // Assert
        assertNotNull(request);
        assertEquals("John Doe", request.getUserName());
        assertEquals("john@example.com", request.getUserEmail());
        assertEquals("My Title", request.getTitle());
        assertEquals("My Content", request.getContent());
        assertEquals("Google", request.getCompanyTag());
        assertEquals(image, request.getImage());
    }

    @Test
    void image_CanBeNull() {
        // Arrange
        InterviewExperienceRequest request = InterviewExperienceRequest.builder()
                .userName("John Doe")
                .userEmail("john@example.com")
                .title("Title")
                .content("Content that is long enough")
                .companyTag("Company")
                .build();

        // Assert
        assertNull(request.getImage());
    }

    @Test
    void request_WithMinimalValidData() {
        // Arrange & Act
        InterviewExperienceRequest request = InterviewExperienceRequest.builder()
                .userName("J")
                .userEmail("valid@email.com")
                .title("Min")
                .content("Valid content that is at least ten characters long")
                .companyTag("C")
                .build();

        // Assert
        assertNotNull(request);
        assertEquals("J", request.getUserName());
        assertEquals("valid@email.com", request.getUserEmail());
        assertEquals("Min", request.getTitle());
        assertTrue(request.getContent().length() >= 10);
    }

    @Test
    void request_WithMaximalData() {
        // Arrange
        String longTitle = "A".repeat(100);
        String longContent = "B".repeat(1000);
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test".getBytes());

        // Act
        InterviewExperienceRequest request = InterviewExperienceRequest.builder()
                .userName("Very Long Name Here")
                .userEmail("verylongemailaddress@verylongdomain.com")
                .title(longTitle)
                .content(longContent)
                .companyTag("Very Long Company Name")
                .image(image)
                .build();

        // Assert
        assertNotNull(request);
        assertEquals(100, request.getTitle().length());
        assertEquals(1000, request.getContent().length());
    }
}
