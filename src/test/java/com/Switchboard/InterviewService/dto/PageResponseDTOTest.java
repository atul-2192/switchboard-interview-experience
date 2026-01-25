package com.Switchboard.InterviewService.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PageResponseDTOTest {

    @Test
    void builder_ShouldCreatePageResponse() {
        // Arrange
        List<InterviewExperienceResponse> content = Arrays.asList(
                createMockResponse("User 1", "user1@example.com"),
                createMockResponse("User 2", "user2@example.com")
        );

        // Act
        PageResponseDTO pageResponse = PageResponseDTO.builder()
                .content(content)
                .pageNumber(0)
                .pageSize(10)
                .totalElements(2)
                .totalPages(1)
                .lastPage(true)
                .build();

        // Assert
        assertNotNull(pageResponse);
        assertEquals(content, pageResponse.getContent());
        assertEquals(0, pageResponse.getPageNumber());
        assertEquals(10, pageResponse.getPageSize());
        assertEquals(2, pageResponse.getTotalElements());
        assertEquals(1, pageResponse.getTotalPages());
        assertTrue(pageResponse.isLastPage());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Arrange
        PageResponseDTO pageResponse = new PageResponseDTO();
        List<InterviewExperienceResponse> content = Arrays.asList(
                createMockResponse("User 1", "user1@example.com")
        );

        // Act
        pageResponse.setContent(content);
        pageResponse.setPageNumber(2);
        pageResponse.setPageSize(20);
        pageResponse.setTotalElements(100);
        pageResponse.setTotalPages(5);
        pageResponse.setLastPage(false);

        // Assert
        assertEquals(content, pageResponse.getContent());
        assertEquals(2, pageResponse.getPageNumber());
        assertEquals(20, pageResponse.getPageSize());
        assertEquals(100, pageResponse.getTotalElements());
        assertEquals(5, pageResponse.getTotalPages());
        assertFalse(pageResponse.isLastPage());
    }

    @Test
    void noArgsConstructor_ShouldCreateEmptyPageResponse() {
        // Act
        PageResponseDTO pageResponse = new PageResponseDTO();

        // Assert
        assertNotNull(pageResponse);
        assertNull(pageResponse.getContent());
        assertEquals(0, pageResponse.getPageNumber());
        assertEquals(0, pageResponse.getPageSize());
        assertEquals(0, pageResponse.getTotalElements());
        assertEquals(0, pageResponse.getTotalPages());
        assertFalse(pageResponse.isLastPage());
    }

    @Test
    void allArgsConstructor_ShouldCreateFullPageResponse() {
        // Arrange
        List<InterviewExperienceResponse> content = Arrays.asList(
                createMockResponse("User 1", "user1@example.com")
        );

        // Act
        PageResponseDTO pageResponse = new PageResponseDTO(content, 1, 15, 45, 3, false);

        // Assert
        assertNotNull(pageResponse);
        assertEquals(content, pageResponse.getContent());
        assertEquals(1, pageResponse.getPageNumber());
        assertEquals(15, pageResponse.getPageSize());
        assertEquals(45, pageResponse.getTotalElements());
        assertEquals(3, pageResponse.getTotalPages());
        assertFalse(pageResponse.isLastPage());
    }

    @Test
    void pageResponse_WithEmptyContent() {
        // Arrange & Act
        PageResponseDTO pageResponse = PageResponseDTO.builder()
                .content(Collections.emptyList())
                .pageNumber(0)
                .pageSize(10)
                .totalElements(0)
                .totalPages(0)
                .lastPage(true)
                .build();

        // Assert
        assertNotNull(pageResponse.getContent());
        assertTrue(pageResponse.getContent().isEmpty());
        assertEquals(0, pageResponse.getTotalElements());
        assertTrue(pageResponse.isLastPage());
    }

    @Test
    void pageResponse_FirstPage() {
        // Arrange
        List<InterviewExperienceResponse> content = Arrays.asList(
                createMockResponse("User 1", "user1@example.com"),
                createMockResponse("User 2", "user2@example.com")
        );

        // Act
        PageResponseDTO pageResponse = PageResponseDTO.builder()
                .content(content)
                .pageNumber(0)
                .pageSize(10)
                .totalElements(25)
                .totalPages(3)
                .lastPage(false)
                .build();

        // Assert
        assertEquals(0, pageResponse.getPageNumber());
        assertFalse(pageResponse.isLastPage());
        assertEquals(3, pageResponse.getTotalPages());
    }

    @Test
    void pageResponse_LastPage() {
        // Arrange
        List<InterviewExperienceResponse> content = Arrays.asList(
                createMockResponse("User 1", "user1@example.com")
        );

        // Act
        PageResponseDTO pageResponse = PageResponseDTO.builder()
                .content(content)
                .pageNumber(2)
                .pageSize(10)
                .totalElements(21)
                .totalPages(3)
                .lastPage(true)
                .build();

        // Assert
        assertEquals(2, pageResponse.getPageNumber());
        assertTrue(pageResponse.isLastPage());
        assertEquals(3, pageResponse.getTotalPages());
    }

    @Test
    void pageResponse_MiddlePage() {
        // Arrange
        List<InterviewExperienceResponse> content = Arrays.asList(
                createMockResponse("User 1", "user1@example.com"),
                createMockResponse("User 2", "user2@example.com")
        );

        // Act
        PageResponseDTO pageResponse = PageResponseDTO.builder()
                .content(content)
                .pageNumber(1)
                .pageSize(10)
                .totalElements(30)
                .totalPages(3)
                .lastPage(false)
                .build();

        // Assert
        assertEquals(1, pageResponse.getPageNumber());
        assertFalse(pageResponse.isLastPage());
        assertEquals(3, pageResponse.getTotalPages());
    }

    @Test
    void pageResponse_WithLargeDataset() {
        // Arrange
        List<InterviewExperienceResponse> content = Arrays.asList(
                createMockResponse("User 1", "user1@example.com")
        );

        // Act
        PageResponseDTO pageResponse = PageResponseDTO.builder()
                .content(content)
                .pageNumber(50)
                .pageSize(100)
                .totalElements(10000)
                .totalPages(100)
                .lastPage(false)
                .build();

        // Assert
        assertEquals(50, pageResponse.getPageNumber());
        assertEquals(100, pageResponse.getPageSize());
        assertEquals(10000, pageResponse.getTotalElements());
        assertEquals(100, pageResponse.getTotalPages());
    }

    @Test
    void pageResponse_SingleElementPage() {
        // Arrange
        List<InterviewExperienceResponse> content = Arrays.asList(
                createMockResponse("Only User", "only@example.com")
        );

        // Act
        PageResponseDTO pageResponse = PageResponseDTO.builder()
                .content(content)
                .pageNumber(0)
                .pageSize(1)
                .totalElements(1)
                .totalPages(1)
                .lastPage(true)
                .build();

        // Assert
        assertEquals(1, pageResponse.getContent().size());
        assertEquals(1, pageResponse.getPageSize());
        assertEquals(1, pageResponse.getTotalElements());
        assertTrue(pageResponse.isLastPage());
    }

    private InterviewExperienceResponse createMockResponse(String userName, String userEmail) {
        return InterviewExperienceResponse.builder()
                .id(UUID.randomUUID())
                .userName(userName)
                .userEmail(userEmail)
                .title("Interview Experience")
                .content("Content of the interview experience.")
                .companyTag("TechCorp")
                .imageName("https://s3.amazonaws.com/bucket/image.jpg")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
