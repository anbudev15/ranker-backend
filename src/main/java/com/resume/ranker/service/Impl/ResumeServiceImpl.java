package com.resume.ranker.service.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resume.ranker.dto.ResumeUploadRequest;
import com.resume.ranker.model.Resume;
import com.resume.ranker.repository.ResumeRepository;
import com.resume.ranker.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ResumeServiceImpl implements ResumeService {

    @Autowired
    private ResumeRepository resumeRepository;

    @Value("${resume.upload.dir}")
    private String uploadDir;

    public void saveResume(MultipartFile file, ResumeUploadRequest request) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File savedFile = new File(uploadDir + File.separator + fileName);
        try (FileOutputStream fos = new FileOutputStream(savedFile)) {
            fos.write(file.getBytes());
        }
        Resume resume = new Resume();
        resume.setName(request.getName());
        resume.setEmail(request.getEmail());
        resume.setResumePath(savedFile.getAbsolutePath());
        resume.setSkills("");
        resume.setScore(0.0);
        List<String> skills = extractSkills(savedFile.getAbsolutePath());
        String skillString = String.join(", ", skills);
        resume.setSkills(skillString);

        resumeRepository.save(resume);
    }

    @Override
    public List<Resume> getAllResumes() {
        return resumeRepository.findAll();
    }

    @Override
    public Resume getResumeById(Long id) {
        return resumeRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteResume(Long id) {
        resumeRepository.deleteById(id);
    }

    public static List<String> extractSkills(String resumePath) {
        List<String> skills = new ArrayList<>();
        try {
            // Python command to run
            ProcessBuilder processBuilder = new ProcessBuilder("python3", "python/extract_skills.py", resumePath);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // Read output from Python
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String outputLine;
            StringBuilder jsonOutput = new StringBuilder();
            while ((outputLine = reader.readLine()) != null) {
                jsonOutput.append(outputLine);
            }

            // Parse the output (assuming JSON array)
            String jsonString = jsonOutput.toString().replaceAll("[\\[\\]\"]", "");
            String[] skillArray = jsonString.split(",");
            for (String skill : skillArray) {
                skills.add(skill.trim());
            }

            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return skills;
    }

    public Resume processResumeWithPython(Resume resume, String jobDescription) throws IOException, InterruptedException {
        String pythonScript = "python/extract_skills.py";

        ProcessBuilder pb = new ProcessBuilder("python", pythonScript, resume.getResumePath(), jobDescription);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder jsonOutput = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonOutput.append(line);
        }

        int exitCode = process.waitFor();
        if (exitCode == 0) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(jsonOutput.toString());

            List<String> skills = new ArrayList<>();
            node.get("skills").forEach(skill -> skills.add(skill.asText()));

            double score = node.get("score").asDouble();

            resume.setSkills(String.join(",", skills));
            resume.setScore(score);

            return resumeRepository.save(resume);
        } else {
            throw new RuntimeException("Python script failed.");
        }
    }

}
