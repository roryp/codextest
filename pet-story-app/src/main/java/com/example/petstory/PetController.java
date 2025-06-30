package com.example.petstory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Controller
public class PetController {

    private static final Logger logger = LoggerFactory.getLogger(PetController.class);
    
    // Allowed image file types
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );
    
    // Max file size (10MB)
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    private final ImageService imageService;
    private final StoryService storyService;

    public PetController(ImageService imageService, StoryService storyService) {
        this.imageService = imageService;
        this.storyService = storyService;
    }

    @GetMapping("/")
    public String index() {
        logger.info("Accessing index page");
        return "index";
    }

    @PostMapping("/upload")
    public String handleUpload(@RequestParam("image") @NotNull MultipartFile file, 
                             Model model, 
                             RedirectAttributes redirectAttributes) {
        
        logger.info("Received file upload request. File: {}, Size: {}", 
                   file.getOriginalFilename(), file.getSize());
        
        try {
            // Validate file
            if (file.isEmpty()) {
                logger.warn("Empty file uploaded");
                redirectAttributes.addFlashAttribute("error", "Please select a file to upload.");
                return "redirect:/";
            }
            
            if (file.getSize() > MAX_FILE_SIZE) {
                logger.warn("File too large: {} bytes", file.getSize());
                redirectAttributes.addFlashAttribute("error", "File size must be less than 10MB.");
                return "redirect:/";
            }
            
            if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
                logger.warn("Invalid file type: {}", file.getContentType());
                redirectAttributes.addFlashAttribute("error", "Please upload a valid image file (JPEG, PNG, GIF, WebP).");
                return "redirect:/";
            }
            
            // Create secure temporary file
            Path tempFile = null;
            try {
                tempFile = Files.createTempFile("pet-upload-", getFileExtension(file.getOriginalFilename()));
                file.transferTo(tempFile.toFile());
                
                logger.info("Processing image file: {}", tempFile);
                
                // Generate caption and story
                String caption = imageService.caption(tempFile.toFile());
                logger.info("Generated caption: {}", caption);
                
                // Sanitize caption before generating story
                String sanitizedCaption = sanitizeInput(caption);
                String story = storyService.generateStory(sanitizedCaption);
                logger.info("Generated story of length: {}", story.length());
                
                // Add results to model
                model.addAttribute("caption", caption);
                model.addAttribute("story", story);
                model.addAttribute("fileName", file.getOriginalFilename());
                
                return "result";
                
            } finally {
                // Ensure temporary file is cleaned up
                if (tempFile != null) {
                    try {
                        Files.deleteIfExists(tempFile);
                        logger.debug("Cleaned up temporary file: {}", tempFile);
                    } catch (IOException e) {
                        logger.error("Failed to delete temporary file: {}", tempFile, e);
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("Error processing file upload", e);
            redirectAttributes.addFlashAttribute("error", "An error occurred while processing your image. Please try again.");
            return "redirect:/";
        }
    }
    
    /**
     * Sanitize input to prevent injection attacks
     */
    private String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }
        // Remove potentially dangerous characters and limit length
        return input.replaceAll("[<>\"'&]", "")
                   .trim()
                   .substring(0, Math.min(input.length(), 500));
    }
    
    /**
     * Get file extension from filename
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return ".tmp";
        }
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot) : ".tmp";
    }
}
