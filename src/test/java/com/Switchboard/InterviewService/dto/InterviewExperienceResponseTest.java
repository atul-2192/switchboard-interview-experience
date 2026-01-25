package com.Switchboard.InterviewService.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InterviewExperienceResponseTest {

    @Test
    void builder_ShouldCreateResponse() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        // Act
        InterviewExperienceResponse response = InterviewExperienceResponse.builder()
                .id(id)
                .userName("John Doe")
                .userEmail("john.doe@example.com")
                .title("My Interview Experience")
                .imageName("https://s3.amazonaws.com/bucket/image.jpg")
                .content("This is the content of my interview.")
                .companyTag("Google")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Assert
        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("John Doe", response.getUserName());
        assertEquals("john.doe@example.com", response.getUserEmail());
        assertEquals("My Interview Experience", response.getTitle());
        assertEquals("https://s3.amazonaws.com/bucket/image.jpg", response.getImageName());
        assertEquals("This is the content of my interview.", response.getContent());
        assertEquals("Google", response.getCompanyTag());
        assertEquals(now, response.getCreatedAt());
        assertEquals(now, response.getUpdatedAt());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Arrange
        InterviewExperienceResponse response = new InterviewExperienceResponse();
        UUID id = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        // Act
        response.setId(id);
        response.setUserName("Jane Smith");
        response.setUserEmail("jane.smith@example.com");
        response.setTitle("Backend Interview");
        response.setImageName("https://s3.amazonaws.com/bucket/test.jpg");
        response.setContent("Detailed interview content.");
        response.setCompanyTag("Amazon");
        response.setCreatedAt(createdAt);
        response.setUpdatedAt(updatedAt);

        // Assert
        assertEquals(id, response.getId());
        assertEquals("Jane Smith", response.getUserName());
        assertEquals("jane.smith@example.com", response.getUserEmail());
        assertEquals("Backend Interview", response.getTitle());
        assertEquals("https://s3.amazonaws.com/bucket/test.jpg", response.getImageName());
        assertEquals("Detailed interview content.", response.getContent());
        assertEquals("Amazon", response.getCompanyTag());
        assertEquals(createdAt, response.getCreatedAt());
        assertEquals(updatedAt, response.getUpdatedAt());
    }

    @Test
    void noArgsConstructor_ShouldCreateEmptyResponse() {
        // Act
        InterviewExperienceResponse response = new InterviewExperienceResponse();

        // Assert
        assertNotNull(response);
        assertNull(response.getId());
        assertNull(response.getUserName());
        assertNull(response.getUserEmail());
        assertNull(response.getTitle());
        assertNull(response.getImageName());
        assertNull(response.getContent());
        assertNull(response.getCompanyTag());
        assertNull(response.getCreatedAt());
        assertNull(response.getUpdatedAt());
    }

    @Test
    void allArgsConstructor_ShouldCreateFullResponse() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        // Act
        InterviewExperienceResponse response = new InterviewExperienceResponse(
                id,
                "John Doe",
                "john@example.com",
                "My Title",
                "https://s3.amazonaws.com/bucket/image.jpg",
                "My Content",
                "Google",
                now,
                now
        );

        // Assert
        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("John Doe", response.getUserName());
        assertEquals("john@example.com", response.getUserEmail());
        assertEquals("My Title", response.getTitle());
        assertEquals("https://s3.amazonaws.com/bucket/image.jpg", response.getImageName());
        assertEquals("My Content", response.getContent());
        assertEquals("Google", response.getCompanyTag());
        assertEquals(now, response.getCreatedAt());
        assertEquals(now, response.getUpdatedAt());
    }

    @Test
    void imageName_CanBeNull() {
        // Arrange
        InterviewExperienceResponse response = InterviewExperienceResponse.builder()
                .id(UUID.randomUUID())
                .userName("John Doe")
                .userEmail("john@example.com")
                .title("Title")
                .content("Content")
                .companyTag("Company")
                .build();

        // Assert
        assertNull(response.getImageName());
    }

    @Test
    void response_WithAllFields() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.of(2025, 1, 1, 10, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2025, 1, 2, 15, 30);

        // Act
        InterviewExperienceResponse response = InterviewExperienceResponse.builder()
                .id(id)
                .userName("Alice Johnson")
                .userEmail("alice.johnson@techcorp.com")
                .title("Senior Software Engineer Interview at TechCorp")
                .imageName("https://s3.amazonaws.com/interview-bucket/alice-interview.jpg")
                .content("Had a great experience interviewing at TechCorp. The process was smooth...")
                .companyTag("TechCorp")
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        // Assert
        assertEquals(id, response.getId());
        assertEquals("Alice Johnson", response.getUserName());
        assertEquals("alice.johnson@techcorp.com", response.getUserEmail());
        assertEquals("Senior Software Engineer Interview at TechCorp", response.getTitle());
        assertEquals("https://s3.amazonaws.com/interview-bucket/alice-interview.jpg", response.getImageName());
        assertEquals("Had a great experience interviewing at TechCorp. The process was smooth...", response.getContent());
        assertEquals("TechCorp", response.getCompanyTag());
        assertEquals(createdAt, response.getCreatedAt());
        assertEquals(updatedAt, response.getUpdatedAt());
    }

    @Test
    void timestamps_CanBeDifferent() {
        // Arrange
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();

        // Act
        InterviewExperienceResponse response = InterviewExperienceResponse.builder()
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        // Assert
        assertTrue(response.getUpdatedAt().isAfter(response.getCreatedAt()));
    }
}
