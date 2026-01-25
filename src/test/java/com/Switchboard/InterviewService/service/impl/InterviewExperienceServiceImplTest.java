package com.Switchboard.InterviewService.service.impl;

import com.Switchboard.InterviewService.dto.InterviewExperienceRequest;
import com.Switchboard.InterviewService.dto.InterviewExperienceResponse;
import com.Switchboard.InterviewService.dto.PageResponseDTO;
import com.Switchboard.InterviewService.model.InterviewExperience;
import com.Switchboard.InterviewService.repository.InterviewExperienceRepository;
import com.Switchboard.InterviewService.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterviewExperienceServiceImplTest {

    @Mock
    private InterviewExperienceRepository repository;

    @Mock
    private FileService fileService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private InterviewExperienceServiceImpl service;

    private InterviewExperienceRequest request;
    private InterviewExperience entity;
    private InterviewExperienceResponse response;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        
        request = InterviewExperienceRequest.builder()
                .userName("Jane Smith")
                .userEmail("jane.smith@example.com")
                .title("Software Engineer Interview at Amazon")
                .content("Detailed interview experience content here...")
                .companyTag("Amazon")
                .build();

        entity = InterviewExperience.builder()
                .id(testId)
                .userName("Jane Smith")
                .userEmail("jane.smith@example.com")
                .title("Software Engineer Interview at Amazon")
                .content("Detailed interview experience content here...")
                .companyTag("Amazon")
                .imageName("https://s3.amazonaws.com/bucket/image.jpg")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        response = InterviewExperienceResponse.builder()
                .id(testId)
                .userName("Jane Smith")
                .userEmail("jane.smith@example.com")
                .title("Software Engineer Interview at Amazon")
                .content("Detailed interview experience content here...")
                .companyTag("Amazon")
                .imageName("https://s3.amazonaws.com/bucket/image.jpg")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createInterviewExperience_WithoutImage_ShouldSaveAndReturnResponse() {
        // Arrange
        when(modelMapper.map(request, InterviewExperience.class)).thenReturn(entity);
        when(repository.save(any(InterviewExperience.class))).thenReturn(entity);
        when(modelMapper.map(entity, InterviewExperienceResponse.class)).thenReturn(response);

        // Act
        InterviewExperienceResponse result = service.createInterviewExperience(request, null);

        // Assert
        assertNotNull(result);
        assertEquals(response.getUserName(), result.getUserName());
        assertEquals(response.getUserEmail(), result.getUserEmail());
        verify(repository, times(1)).save(any(InterviewExperience.class));
    }

    @Test
    void createInterviewExperience_WithImage_ShouldSetImageUrlAndSave() {
        // Arrange
        String imageUrl = "https://s3.amazonaws.com/bucket/new-image.jpg";
        when(modelMapper.map(request, InterviewExperience.class)).thenReturn(entity);
        when(repository.save(any(InterviewExperience.class))).thenReturn(entity);
        when(modelMapper.map(entity, InterviewExperienceResponse.class)).thenReturn(response);

        // Act
        InterviewExperienceResponse result = service.createInterviewExperience(request, imageUrl);

        // Assert
        assertNotNull(result);
        verify(repository, times(1)).save(argThat(exp -> 
            exp.getImageName() != null && exp.getImageName().equals(imageUrl)
        ));
    }

    @Test
    void searchByEmail_ShouldReturnListOfExperiences() {
        // Arrange
        String email = "jane.smith@example.com";
        List<InterviewExperience> experiences = Arrays.asList(entity);
        when(repository.findByUserEmailOrderByCreatedAtDesc(email)).thenReturn(experiences);
        when(modelMapper.map(entity, InterviewExperienceResponse.class)).thenReturn(response);

        // Act
        List<InterviewExperienceResponse> result = service.searchByEmail(email);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(response.getUserEmail(), result.get(0).getUserEmail());
        verify(repository, times(1)).findByUserEmailOrderByCreatedAtDesc(email);
    }

    @Test
    void searchByEmail_WithNoResults_ShouldReturnEmptyList() {
        // Arrange
        String email = "nonexistent@example.com";
        when(repository.findByUserEmailOrderByCreatedAtDesc(email)).thenReturn(Arrays.asList());

        // Act
        List<InterviewExperienceResponse> result = service.searchByEmail(email);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository, times(1)).findByUserEmailOrderByCreatedAtDesc(email);
    }

    @Test
    void searchByCompany_ShouldReturnListOfExperiences() {
        // Arrange
        String company = "Amazon";
        List<InterviewExperience> experiences = Arrays.asList(entity);
        when(repository.findByCompanyTagOrderByCreatedAtDesc(company)).thenReturn(experiences);
        when(modelMapper.map(entity, InterviewExperienceResponse.class)).thenReturn(response);

        // Act
        List<InterviewExperienceResponse> result = service.searchByCompany(company);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(response.getCompanyTag(), result.get(0).getCompanyTag());
        verify(repository, times(1)).findByCompanyTagOrderByCreatedAtDesc(company);
    }

    @Test
    void searchByCompany_WithNoResults_ShouldReturnEmptyList() {
        // Arrange
        String company = "NonExistentCompany";
        when(repository.findByCompanyTagOrderByCreatedAtDesc(company)).thenReturn(Arrays.asList());

        // Act
        List<InterviewExperienceResponse> result = service.searchByCompany(company);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository, times(1)).findByCompanyTagOrderByCreatedAtDesc(company);
    }

    @Test
    void getAllInterviews_WithAscendingSort_ShouldReturnPagedResponse() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("updatedAt").ascending());
        List<InterviewExperience> experiences = Arrays.asList(entity);
        Page<InterviewExperience> page = new PageImpl<>(experiences, pageRequest, 1);
        
        when(repository.findAll(any(PageRequest.class))).thenReturn(page);
        when(modelMapper.map(entity, InterviewExperienceResponse.class)).thenReturn(response);

        // Act
        PageResponseDTO result = service.getAllInterviews(0, 10, "updatedAt", "asc");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(0, result.getPageNumber());
        assertEquals(10, result.getPageSize());
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertTrue(result.isLastPage());
        verify(repository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    void getAllInterviews_WithDescendingSort_ShouldReturnPagedResponse() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(1, 20, Sort.by("createdAt").descending());
        List<InterviewExperience> experiences = Arrays.asList(entity);
        Page<InterviewExperience> page = new PageImpl<>(experiences, pageRequest, 25);
        
        when(repository.findAll(any(PageRequest.class))).thenReturn(page);
        when(modelMapper.map(entity, InterviewExperienceResponse.class)).thenReturn(response);

        // Act
        PageResponseDTO result = service.getAllInterviews(1, 20, "createdAt", "desc");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getPageNumber());
        assertEquals(20, result.getPageSize());
        verify(repository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    void getInterviewById_WhenExists_ShouldReturnResponse() {
        // Arrange
        when(repository.findById(testId)).thenReturn(Optional.of(entity));
        when(modelMapper.map(entity, InterviewExperienceResponse.class)).thenReturn(response);

        // Act
        InterviewExperienceResponse result = service.getInterviewById(testId);

        // Assert
        assertNotNull(result);
        assertEquals(testId, result.getId());
        assertEquals(response.getTitle(), result.getTitle());
        verify(repository, times(1)).findById(testId);
    }

    @Test
    void getInterviewById_WhenNotFound_ShouldThrowException() {
        // Arrange
        when(repository.findById(testId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.getInterviewById(testId);
        });
        
        assertEquals("Interview Experience not found", exception.getMessage());
        verify(repository, times(1)).findById(testId);
    }

    @Test
    void deleteInterviewExperience_WithoutImage_ShouldDeleteEntity() {
        // Arrange
        entity.setImageName(null);
        when(repository.findById(testId)).thenReturn(Optional.of(entity));
        doNothing().when(repository).delete(entity);

        // Act
        service.deleteInterviewExperience(testId);

        // Assert
        verify(repository, times(1)).findById(testId);
        verify(repository, times(1)).delete(entity);
        verify(fileService, never()).deleteImage(anyString());
    }

    @Test
    void deleteInterviewExperience_WithImage_ShouldDeleteImageAndEntity() {
        // Arrange
        when(repository.findById(testId)).thenReturn(Optional.of(entity));
        doNothing().when(fileService).deleteImage(entity.getImageName());
        doNothing().when(repository).delete(entity);

        // Act
        service.deleteInterviewExperience(testId);

        // Assert
        verify(repository, times(1)).findById(testId);
        verify(fileService, times(1)).deleteImage(entity.getImageName());
        verify(repository, times(1)).delete(entity);
    }

    @Test
    void deleteInterviewExperience_WithEmptyImageName_ShouldNotDeleteImage() {
        // Arrange
        entity.setImageName("");
        when(repository.findById(testId)).thenReturn(Optional.of(entity));
        doNothing().when(repository).delete(entity);

        // Act
        service.deleteInterviewExperience(testId);

        // Assert
        verify(repository, times(1)).findById(testId);
        verify(fileService, never()).deleteImage(anyString());
        verify(repository, times(1)).delete(entity);
    }

    @Test
    void deleteInterviewExperience_WhenNotFound_ShouldThrowException() {
        // Arrange
        when(repository.findById(testId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.deleteInterviewExperience(testId);
        });
        
        assertEquals("Interview Experience not found", exception.getMessage());
        verify(repository, times(1)).findById(testId);
        verify(repository, never()).delete(any());
    }

    @Test
    void deleteInterviewExperience_WhenS3DeleteFails_ShouldStillDeleteEntity() {
        // Arrange
        when(repository.findById(testId)).thenReturn(Optional.of(entity));
        doThrow(new RuntimeException("S3 delete failed")).when(fileService).deleteImage(anyString());
        doNothing().when(repository).delete(entity);

        // Act
        service.deleteInterviewExperience(testId);

        // Assert
        verify(repository, times(1)).findById(testId);
        verify(fileService, times(1)).deleteImage(entity.getImageName());
        verify(repository, times(1)).delete(entity);
    }

    @Test
    void updateInterviewExperience_WithoutNewImage_ShouldUpdateFields() throws IOException {
        // Arrange
        when(repository.findById(testId)).thenReturn(Optional.of(entity));
        when(repository.save(any(InterviewExperience.class))).thenReturn(entity);
        when(modelMapper.map(entity, InterviewExperienceResponse.class)).thenReturn(response);

        // Act
        InterviewExperienceResponse result = service.updateInterviewExperience(testId, request, null);

        // Assert
        assertNotNull(result);
        verify(repository, times(1)).findById(testId);
        verify(repository, times(1)).save(any(InterviewExperience.class));
        verify(fileService, never()).uploadImage(anyString(), any());
        verify(fileService, never()).deleteImage(anyString());
    }

    @Test
    void updateInterviewExperience_WithEmptyImage_ShouldNotUploadOrDelete() throws IOException {
        // Arrange
        MockMultipartFile emptyFile = new MockMultipartFile("image", "", "image/jpeg", new byte[0]);
        when(repository.findById(testId)).thenReturn(Optional.of(entity));
        when(repository.save(any(InterviewExperience.class))).thenReturn(entity);
        when(modelMapper.map(entity, InterviewExperienceResponse.class)).thenReturn(response);

        // Act
        InterviewExperienceResponse result = service.updateInterviewExperience(testId, request, emptyFile);

        // Assert
        assertNotNull(result);
        verify(fileService, never()).uploadImage(anyString(), any());
        verify(fileService, never()).deleteImage(anyString());
    }

    @Test
    void updateInterviewExperience_WithNewImage_ShouldDeleteOldAndUploadNew() throws IOException {
        // Arrange
        MockMultipartFile newFile = new MockMultipartFile(
                "image",
                "new-image.jpg",
                "image/jpeg",
                "new image content".getBytes()
        );
        String newImageUrl = "https://s3.amazonaws.com/bucket/new-image.jpg";
        String oldImageUrl = entity.getImageName(); // Get old image URL before the test
        
        when(repository.findById(testId)).thenReturn(Optional.of(entity));
        doNothing().when(fileService).deleteImage(anyString());
        when(fileService.uploadImage(anyString(), any())).thenReturn(newImageUrl);
        when(repository.save(any(InterviewExperience.class))).thenReturn(entity);
        when(modelMapper.map(entity, InterviewExperienceResponse.class)).thenReturn(response);

        // Act
        InterviewExperienceResponse result = service.updateInterviewExperience(testId, request, newFile);

        // Assert
        assertNotNull(result);
        verify(fileService, times(1)).deleteImage(oldImageUrl);  // Verify old image was deleted
        verify(fileService, times(1)).uploadImage(anyString(), any());
        verify(repository, times(1)).save(any(InterviewExperience.class));
    }

    @Test
    void updateInterviewExperience_WithNewImageButNoOldImage_ShouldOnlyUploadNew() throws IOException {
        // Arrange
        entity.setImageName(null);
        MockMultipartFile newFile = new MockMultipartFile(
                "image",
                "new-image.jpg",
                "image/jpeg",
                "new image content".getBytes()
        );
        String newImageUrl = "https://s3.amazonaws.com/bucket/new-image.jpg";
        
        when(repository.findById(testId)).thenReturn(Optional.of(entity));
        when(fileService.uploadImage(anyString(), any())).thenReturn(newImageUrl);
        when(repository.save(any(InterviewExperience.class))).thenReturn(entity);
        when(modelMapper.map(entity, InterviewExperienceResponse.class)).thenReturn(response);

        // Act
        InterviewExperienceResponse result = service.updateInterviewExperience(testId, request, newFile);

        // Assert
        assertNotNull(result);
        verify(fileService, never()).deleteImage(anyString());
        verify(fileService, times(1)).uploadImage(anyString(), any());
    }

    @Test
    void updateInterviewExperience_WhenNotFound_ShouldThrowException() {
        // Arrange
        when(repository.findById(testId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.updateInterviewExperience(testId, request, null);
        });
        
        assertEquals("Interview Experience not found", exception.getMessage());
        verify(repository, times(1)).findById(testId);
        verify(repository, never()).save(any());
    }

    @Test
    void updateInterviewExperience_ShouldUpdateAllFields() throws IOException {
        // Arrange
        when(repository.findById(testId)).thenReturn(Optional.of(entity));
        when(repository.save(any(InterviewExperience.class))).thenReturn(entity);
        when(modelMapper.map(entity, InterviewExperienceResponse.class)).thenReturn(response);

        // Act
        service.updateInterviewExperience(testId, request, null);

        // Assert
        verify(repository, times(1)).save(argThat(exp ->
            exp.getUserName().equals(request.getUserName()) &&
            exp.getUserEmail().equals(request.getUserEmail()) &&
            exp.getTitle().equals(request.getTitle()) &&
            exp.getContent().equals(request.getContent()) &&
            exp.getCompanyTag().equals(request.getCompanyTag())
        ));
    }
}
