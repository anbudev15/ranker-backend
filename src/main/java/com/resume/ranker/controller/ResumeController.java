package com.resume.ranker.controller;

import com.resume.ranker.model.Resume;
import com.resume.ranker.service.ResumeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/resumes")
@Tag(name = "Resume API", description = "Endpoints for uploading and managing resumes")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadResume(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam MultipartFile file) throws IOException {

        try {
            // Step 1: Save resume and create initial Resume object
            Resume savedResume = resumeService.saveResume(file, name, email);

            // Step 2: Extract skills + score using Python script
//            resumeService.processResumeWithPython(savedResume);

            return ResponseEntity.ok("Resume uploaded and processed successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Failed to upload resume: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Resume>> getAllResumes() {
        return ResponseEntity.ok(resumeService.getAllResumes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resume> getResume(@PathVariable Long id) {
        return ResponseEntity.ok(resumeService.getResumeById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResume(@PathVariable Long id) {
        resumeService.deleteResume(id);
        return ResponseEntity.noContent().build();
    }
}
