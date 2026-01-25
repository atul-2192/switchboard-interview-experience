package com.Switchboard.InterviewService.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InterviewExperienceTest {

    private InterviewExperience experience;

    @BeforeEach
    void setUp() {
        experience = new InterviewExperience();
    }

    @Test
    void builder_ShouldCreateInterviewExperience() {
        // Arrange
        UUID id = UUID.randomUUID();
        String userName = "John Doe";
        String userEmail = "john.doe@example.com";
        String title = "My Interview Experience";
        String content = "This is the content of my interview experience.";
        String companyTag = "Google";
        String imageName = "https://s3.amazonaws.com/bucket/image.jpg";
        LocalDateTime now = LocalDateTime.now();

        // Act
        InterviewExperience result = InterviewExperience.builder()
                .id(id)
                .userName(userName)
                .userEmail(userEmail)
                .title(title)
                .content(content)
                .companyTag(companyTag)
                .imageName(imageName)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(userName, result.getUserName());
        assertEquals(userEmail, result.getUserEmail());
        assertEquals(title, result.getTitle());
        assertEquals(content, result.getContent());
        assertEquals(companyTag, result.getCompanyTag());
        assertEquals(imageName, result.getImageName());
        assertEquals(now, result.getCreatedAt());
        assertEquals(now, result.getUpdatedAt());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Arrange
        UUID id = UUID.randomUUID();
        String userName = "Jane Smith";
        String userEmail = "jane.smith@example.com";
        String title = "Backend Interview at Amazon";
        String content = "Detailed content about the interview.";
        String companyTag = "Amazon";
        String imageName = "https://s3.amazonaws.com/bucket/test.jpg";

        // Act
        experience.setId(id);
        experience.setUserName(userName);
        experience.setUserEmail(userEmail);
        experience.setTitle(title);
        experience.setContent(content);
        experience.setCompanyTag(companyTag);
        experience.setImageName(imageName);

        // Assert
        assertEquals(id, experience.getId());
        assertEquals(userName, experience.getUserName());
        assertEquals(userEmail, experience.getUserEmail());
        assertEquals(title, experience.getTitle());
        assertEquals(content, experience.getContent());
        assertEquals(companyTag, experience.getCompanyTag());
        assertEquals(imageName, experience.getImageName());
    }

    @Test
    void noArgsConstructor_ShouldCreateEmptyObject() {
        // Act
        InterviewExperience result = new InterviewExperience();

        // Assert
        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getUserName());
        assertNull(result.getUserEmail());
        assertNull(result.getTitle());
        assertNull(result.getContent());
        assertNull(result.getCompanyTag());
        assertNull(result.getImageName());
    }

    @Test
    void allArgsConstructor_ShouldCreateFullObject() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        // Act
        InterviewExperience result = new InterviewExperience(
                id,
                "John Doe",
                "john@example.com",
                "My Title",
                "My Content",
                "Google",
                "https://s3.amazonaws.com/bucket/image.jpg",
                now,
                now
        );

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("John Doe", result.getUserName());
        assertEquals("john@example.com", result.getUserEmail());
        assertEquals("My Title", result.getTitle());
        assertEquals("My Content", result.getContent());
        assertEquals("Google", result.getCompanyTag());
        assertEquals("https://s3.amazonaws.com/bucket/image.jpg", result.getImageName());
        assertEquals(now, result.getCreatedAt());
        assertEquals(now, result.getUpdatedAt());
    }

    @Test
    void onCreate_ShouldSetTimestamps() {
        // Arrange
        InterviewExperience entity = new InterviewExperience();

        // Act
        entity.onCreate();

        // Assert
        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
        // Both timestamps should be approximately equal (within same millisecond or very close)
        assertTrue(entity.getUpdatedAt().isAfter(entity.getCreatedAt()) || 
                   entity.getUpdatedAt().isEqual(entity.getCreatedAt()));
    }

    @Test
    void onUpdate_ShouldUpdateTimestamp() throws InterruptedException {
        // Arrange
        InterviewExperience entity = new InterviewExperience();
        entity.onCreate();
        LocalDateTime createdAt = entity.getCreatedAt();
        
        // Wait a bit to ensure different timestamp
        Thread.sleep(10);

        // Act
        entity.onUpdate();

        // Assert
        assertNotNull(entity.getUpdatedAt());
        assertEquals(createdAt, entity.getCreatedAt()); // createdAt should not change
        assertTrue(entity.getUpdatedAt().isAfter(createdAt) || entity.getUpdatedAt().isEqual(createdAt));
    }

    @Test
    void imageName_CanBeNull() {
        // Arrange & Act
        experience.setImageName(null);

        // Assert
        assertNull(experience.getImageName());
    }

    @Test
    void imageName_CanBeEmpty() {
        // Arrange & Act
        experience.setImageName("");

        // Assert
        assertEquals("", experience.getImageName());
    }
}
