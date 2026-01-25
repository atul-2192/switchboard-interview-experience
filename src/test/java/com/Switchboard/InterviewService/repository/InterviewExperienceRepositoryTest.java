package com.Switchboard.InterviewService.repository;

import com.Switchboard.InterviewService.model.InterviewExperience;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class InterviewExperienceRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private InterviewExperienceRepository repository;

    private InterviewExperience experience1;
    private InterviewExperience experience2;
    private InterviewExperience experience3;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        entityManager.clear();

        experience1 = InterviewExperience.builder()
                .userName("John Doe")
                .userEmail("john.doe@example.com")
                .title("Interview at Google")
                .content("Great experience at Google with multiple technical rounds.")
                .companyTag("Google")
                .imageName("https://s3.amazonaws.com/bucket/google.jpg")
                .createdAt(LocalDateTime.now().minusDays(2))
                .updatedAt(LocalDateTime.now().minusDays(2))
                .build();

        experience2 = InterviewExperience.builder()
                .userName("Jane Smith")
                .userEmail("jane.smith@example.com")
                .title("Interview at Amazon")
                .content("Amazing interview process at Amazon with behavioral rounds.")
                .companyTag("Amazon")
                .imageName("https://s3.amazonaws.com/bucket/amazon.jpg")
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        experience3 = InterviewExperience.builder()
                .userName("John Doe")
                .userEmail("john.doe@example.com")
                .title("Interview at Microsoft")
                .content("Challenging interview at Microsoft with system design rounds.")
                .companyTag("Microsoft")
                .imageName("https://s3.amazonaws.com/bucket/microsoft.jpg")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void findByUserEmailOrderByCreatedAtDesc_ShouldReturnExperiencesForEmail() {
        // Arrange
        entityManager.persist(experience1);
        entityManager.persist(experience2);
        entityManager.persist(experience3);
        entityManager.flush();

        // Act
        List<InterviewExperience> results = repository.findByUserEmailOrderByCreatedAtDesc("john.doe@example.com");

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("john.doe@example.com", results.get(0).getUserEmail());
        assertEquals("john.doe@example.com", results.get(1).getUserEmail());
        // Should be ordered by createdAt DESC (newest first)
        assertTrue(results.get(0).getCreatedAt().isAfter(results.get(1).getCreatedAt()) ||
                   results.get(0).getCreatedAt().isEqual(results.get(1).getCreatedAt()));
    }

    @Test
    void findByUserEmailOrderByCreatedAtDesc_WithNoMatches_ShouldReturnEmptyList() {
        // Arrange
        entityManager.persist(experience1);
        entityManager.flush();

        // Act
        List<InterviewExperience> results = repository.findByUserEmailOrderByCreatedAtDesc("nonexistent@example.com");

        // Assert
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void findByCompanyTagOrderByCreatedAtDesc_ShouldReturnExperiencesForCompany() {
        // Arrange
        entityManager.persist(experience1);
        entityManager.persist(experience2);
        entityManager.persist(experience3);
        entityManager.flush();

        // Act
        List<InterviewExperience> results = repository.findByCompanyTagOrderByCreatedAtDesc("Google");

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Google", results.get(0).getCompanyTag());
        assertEquals("Interview at Google", results.get(0).getTitle());
    }

    @Test
    void findByCompanyTagOrderByCreatedAtDesc_WithNoMatches_ShouldReturnEmptyList() {
        // Arrange
        entityManager.persist(experience1);
        entityManager.flush();

        // Act
        List<InterviewExperience> results = repository.findByCompanyTagOrderByCreatedAtDesc("Apple");

        // Assert
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void findByCompanyTagOrderByCreatedAtDesc_WithMultipleResults_ShouldOrderByCreatedAtDesc() {
        // Arrange
        InterviewExperience google1 = InterviewExperience.builder()
                .userName("User 1")
                .userEmail("user1@example.com")
                .title("Google Interview 1")
                .content("Content 1")
                .companyTag("Google")
                .createdAt(LocalDateTime.now().minusDays(3))
                .updatedAt(LocalDateTime.now().minusDays(3))
                .build();

        InterviewExperience google2 = InterviewExperience.builder()
                .userName("User 2")
                .userEmail("user2@example.com")
                .title("Google Interview 2")
                .content("Content 2")
                .companyTag("Google")
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        entityManager.persist(google1);
        entityManager.persist(google2);
        entityManager.flush();

        // Act
        List<InterviewExperience> results = repository.findByCompanyTagOrderByCreatedAtDesc("Google");

        // Assert
        assertEquals(2, results.size());
        assertTrue(results.get(0).getCreatedAt().isAfter(results.get(1).getCreatedAt()));
        assertEquals("Google Interview 2", results.get(0).getTitle());
    }

    @Test
    void save_ShouldPersistInterviewExperience() {
        // Arrange
        InterviewExperience newExperience = InterviewExperience.builder()
                .userName("Test User")
                .userEmail("test@example.com")
                .title("Test Interview")
                .content("Test content for interview.")
                .companyTag("TestCorp")
                .imageName("https://s3.amazonaws.com/bucket/test.jpg")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Act
        InterviewExperience saved = repository.save(newExperience);

        // Assert
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("Test User", saved.getUserName());
        assertEquals("test@example.com", saved.getUserEmail());
    }

    @Test
    void findById_ShouldReturnExperience() {
        // Arrange
        InterviewExperience persisted = entityManager.persist(experience1);
        entityManager.flush();
        UUID id = persisted.getId();

        // Act
        var result = repository.findById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getUserName());
    }

    @Test
    void delete_ShouldRemoveExperience() {
        // Arrange
        InterviewExperience persisted = entityManager.persist(experience1);
        entityManager.flush();
        UUID id = persisted.getId();

        // Act
        repository.delete(persisted);
        entityManager.flush();

        // Assert
        var result = repository.findById(id);
        assertFalse(result.isPresent());
    }
}
