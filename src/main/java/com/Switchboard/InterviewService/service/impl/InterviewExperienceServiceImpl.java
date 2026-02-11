package com.Switchboard.InterviewService.service.impl;

import com.Switchboard.InterviewService.config.AppConstants;
import com.Switchboard.InterviewService.dto.InterviewExperienceRequest;
import com.Switchboard.InterviewService.dto.InterviewExperienceResponse;
import com.Switchboard.InterviewService.dto.PageResponseDTO;
import com.Switchboard.InterviewService.model.InterviewExperience;
import com.Switchboard.InterviewService.repository.InterviewExperienceRepository;
import com.Switchboard.InterviewService.service.FileService;
import com.Switchboard.InterviewService.service.InterviewExperienceService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterviewExperienceServiceImpl implements InterviewExperienceService {
    private static final Logger log = LoggerFactory.getLogger(InterviewExperienceServiceImpl.class);

    private final InterviewExperienceRepository repository;
    private final FileService fileService;

    private final ModelMapper modelMapper;

    @Override
    public InterviewExperienceResponse createInterviewExperience(InterviewExperienceRequest request, String imageUrl) {
        log.info("InterviewExperienceServiceImpl :: createInterviewExperience :: Starting");
        InterviewExperience experience = modelMapper.map(request, InterviewExperience.class);
        
        // Set image URL if provided
        if (imageUrl != null) {
            experience.setImageName(imageUrl);
        }

        InterviewExperience newExperience = repository.save(experience);

        log.info("InterviewExperienceServiceImpl :: createInterviewExperience :: Completed successfully");
        return modelMapper.map(newExperience, InterviewExperienceResponse.class);

    }

    @Override
    public List<InterviewExperienceResponse> searchByEmail(String userEmail) {
        log.info("InterviewExperienceServiceImpl :: searchByEmail :: Starting for email: {}", userEmail);
        List<InterviewExperience> experiences = repository.findByUserEmailOrderByCreatedAtDesc(userEmail);

        log.info("InterviewExperienceServiceImpl :: searchByEmail :: Completed successfully - found {} experiences", experiences.size());
        return experiences.stream()
                .map(experience -> modelMapper.map(experience, InterviewExperienceResponse.class))
                .collect(Collectors.toList());
    }


    @Override
    public List<InterviewExperienceResponse> searchByCompany(String companyTag) {
        log.info("InterviewExperienceServiceImpl :: searchByCompany :: Starting for company: {}", companyTag);
        List<InterviewExperience> experiences = repository.findByCompanyTagOrderByCreatedAtDesc(companyTag);

        log.info("InterviewExperienceServiceImpl :: searchByCompany :: Completed successfully - found {} experiences", experiences.size());
        return experiences.stream()
                .map(experience -> modelMapper.map(experience, InterviewExperienceResponse.class))
                .collect(Collectors.toList());
    }


    @Override
    public PageResponseDTO getAllInterviews(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
        log.info("InterviewExperienceServiceImpl :: getAllInterviews :: Starting - page {} with size {}", pageNumber, pageSize);
        Sort sort = (sortDir.equalsIgnoreCase("asc")) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable p = PageRequest.of(pageNumber, pageSize, sort);

        Page<InterviewExperience> experiences = repository.findAll(p);
        List<InterviewExperience> experienceList = experiences.getContent();

        List<InterviewExperienceResponse> res = experienceList.stream()
                .map(experience -> modelMapper.map(experience, InterviewExperienceResponse.class))
                .collect(Collectors.toList());

        log.info("InterviewExperienceServiceImpl :: getAllInterviews :: Completed successfully - found {} experiences", experienceList.size());
        return PageResponseDTO.builder()
                .content(res)
                .pageNumber(experiences.getNumber())
                .pageSize(experiences.getSize())
                .totalElements(experiences.getTotalElements())
                .totalPages(experiences.getTotalPages())
                .lastPage(experiences.isLast())
                .build();
    }

    @Override
    public InterviewExperienceResponse getInterviewById(UUID id) {
        log.info("InterviewExperienceServiceImpl :: getInterviewById :: Starting for id: {}", id);
        InterviewExperience experience = repository.findById(id)
                .orElseThrow(() -> {
                    log.error("InterviewExperienceServiceImpl :: getInterviewById :: Interview Experience not found with id: {}", id);
                    return new RuntimeException("Interview Experience not found");
                });

        log.info("InterviewExperienceServiceImpl :: getInterviewById :: Completed successfully");
        return modelMapper.map(experience, InterviewExperienceResponse.class);
    }


    @Override
    public void deleteInterviewExperience(UUID id) {
        log.info("InterviewExperienceServiceImpl :: deleteInterviewExperience :: Starting for id: {}", id);

        // Fetch existing record
        InterviewExperience experience = repository.findById(id)
                .orElseThrow(() -> {
                    log.error("InterviewExperienceServiceImpl :: deleteInterviewExperience :: Interview Experience not found with id: {}", id);
                    return new RuntimeException("Interview Experience not found");
                });

        // Delete image from S3 if exists
        if (experience.getImageName() != null && !experience.getImageName().isEmpty()) {
            try {
                fileService.deleteImage(experience.getImageName());
            } catch (Exception e) {
                log.error("InterviewExperienceServiceImpl :: deleteInterviewExperience :: Failed to delete image from S3: {}", e.getMessage());
                // Optional: you can throw exception if you want to fail delete if image deletion fails
            }
        }

        // Delete DB record
        repository.delete(experience);
        log.info("InterviewExperienceServiceImpl :: deleteInterviewExperience :: Completed successfully");
    }

    @Override
    public InterviewExperienceResponse updateInterviewExperience(UUID id, InterviewExperienceRequest request, MultipartFile newImage) throws IOException {
        log.info("InterviewExperienceServiceImpl :: updateInterviewExperience :: Starting for id: {}", id);

        // Fetch existing record
        InterviewExperience experience = repository.findById(id)
                .orElseThrow(() -> {
                    log.error("InterviewExperienceServiceImpl :: updateInterviewExperience :: Interview Experience not found with id: {}", id);
                    return new RuntimeException("Interview Experience not found");
                });

        // Handle new image upload
        if (newImage != null && !newImage.isEmpty()) {
            // Delete old image from S3 if exists
            if (experience.getImageName() != null && !experience.getImageName().isEmpty()) {
                fileService.deleteImage(experience.getImageName());
            }

            // Upload new image
            String newImageUrl = fileService.uploadImage(AppConstants.PATH_VARIABLE, newImage);
            experience.setImageName(newImageUrl);
        }

        // Update other fields
        experience.setUserName(request.getUserName());
        experience.setTitle(request.getTitle());
        experience.setCompanyTag(request.getCompanyTag());
        experience.setUserEmail(request.getUserEmail());
        experience.setContent(request.getContent());

        // Save updated entity
        InterviewExperience updatedExperience = repository.save(experience);

        log.info("InterviewExperienceServiceImpl :: updateInterviewExperience :: Completed successfully");
        return modelMapper.map(updatedExperience, InterviewExperienceResponse.class);
    }

}
