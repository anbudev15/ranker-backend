package com.resume.ranker.service;

import com.resume.ranker.model.Resume;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ResumeService {
    Resume saveResume( MultipartFile file,String name, String email) throws IOException;
    List<Resume> getAllResumes();
    Resume getResumeById(Long id);
    void deleteResume(Long id);
//    void processResumeWithPython(Resume savedResume);
}


