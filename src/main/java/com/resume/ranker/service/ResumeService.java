package com.resume.ranker.service;

import com.resume.ranker.dto.ResumeUploadRequest;
import com.resume.ranker.model.Resume;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ResumeService {
    void saveResume(MultipartFile file, ResumeUploadRequest resumeUploadRequest) throws IOException;
    List<Resume> getAllResumes();
    Resume getResumeById(Long id);
    void deleteResume(Long id);
//    void processResumeWithPython(Resume savedResume);
}


