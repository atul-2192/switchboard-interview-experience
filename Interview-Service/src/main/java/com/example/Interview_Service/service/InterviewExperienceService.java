package com.example.Interview_Service.service;

import com.example.Interview_Service.dto.InterviewExperienceRequest;
import com.example.Interview_Service.dto.InterviewExperienceResponse;
import com.example.Interview_Service.dto.PageResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface InterviewExperienceService {

    InterviewExperienceResponse createInterviewExperience(InterviewExperienceRequest request, String imageUrl);

    PageResponseDTO getAllInterviews(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);

    InterviewExperienceResponse  getInterviewById(UUID id);

   List<InterviewExperienceResponse> searchByEmail(String userEmail);

   List<InterviewExperienceResponse> searchByCompany(String companyTag);

   InterviewExperienceResponse updateInterviewExperience(UUID id, InterviewExperienceRequest request, MultipartFile newImage)  throws IOException;

   void deleteInterviewExperience(UUID id);

}
