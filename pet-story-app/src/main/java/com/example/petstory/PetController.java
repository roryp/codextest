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
                
                // Generate caption and story with fallback handling
                String caption;
                String story;
                boolean usingFallback = false;
                
                try {
                    caption = imageService.caption(tempFile.toFile());
                    logger.info("Generated caption using AI service: {}", caption);
                } catch (Exception e) {
                    logger.warn("AI ImageService failed, using fallback: {}", e.getMessage());
                    caption = generateFallbackCaption(file.getOriginalFilename());
                    logger.info("Generated caption using fallback: {}", caption);
                    usingFallback = true;
                }
                
                // Sanitize caption before generating story
                String sanitizedCaption = sanitizeInput(caption);
                
                try {
                    story = storyService.generateStory(sanitizedCaption);
                    logger.info("Generated story using AI service of length: {}", story.length());
                } catch (Exception e) {
                    logger.warn("AI StoryService failed, using fallback: {}", e.getMessage());
                    story = generateFallbackStory(sanitizedCaption);
                    logger.info("Generated story using fallback of length: {}", story.length());
                    usingFallback = true;
                }
                
                // Add results to model
                model.addAttribute("caption", caption);
                model.addAttribute("story", story);
                model.addAttribute("fileName", file.getOriginalFilename());
                if (usingFallback) {
                    model.addAttribute("analysisType", "Fallback analysis (AI service temporarily unavailable)");
                } else {
                    model.addAttribute("analysisType", "AI-powered analysis");
                }
                
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
    
    /**
     * Generate a fallback caption when AI service is unavailable
     */
    private String generateFallbackCaption(String filename) {
        String[] fallbackCaptions = {
            "A beautiful pet captured in a lovely photograph, showing their unique personality and charm.",
            "An adorable companion animal photographed in natural lighting, displaying their gentle nature.",
            "A wonderful pet portrait showcasing their distinctive features and friendly demeanor.",
            "A charming animal friend captured in a moment that highlights their loving character.",
            "A delightful pet photograph that captures their essence and playful spirit beautifully."
        };
        
        // Use filename hash for consistent responses
        int index = Math.abs(filename.hashCode() % fallbackCaptions.length);
        return fallbackCaptions[index];
    }
    
    /**
     * Generate a fallback story when AI service is unavailable
     */
    private String generateFallbackStory(String description) {
        String[] storyTemplates = {
            "Meet the most wonderful pet in the world – a furry ball of energy and love who brings happiness wherever they go! This amazing companion starts each day with an enthusiastic tail wag and is always ready for the next adventure. Whether it's playing in the backyard, going for walks in the neighborhood, or simply enjoying belly rubs, this pet approaches life with boundless enthusiasm and loyalty. They have a special gift for making everyone smile with their playful antics and gentle spirit. At the end of each day, this faithful friend curls up nearby, content knowing they've spread joy and been the best companion anyone could ask for.",
            
            "Once upon a time, there lived a remarkable pet whose heart was as big as their personality! Every morning brought new possibilities for fun and friendship. This special companion had a talent for turning ordinary moments into extraordinary memories. From chasing butterflies in the garden to greeting visitors with unbridled excitement, they filled every day with laughter and love. Their favorite activities included exploring new scents, playing with favorite toys, and sharing quiet moments with their beloved family. This wonderful pet reminded everyone around them that the simple joys in life – a warm sunny spot, a gentle pat, and unconditional love – are truly the most precious gifts of all.",
            
            "In a cozy home filled with love, there lived an extraordinary pet who was everyone's best friend. This delightful companion had a way of brightening even the cloudiest days with their infectious enthusiasm and loving nature. Adventures were their specialty – whether discovering new corners of the house, meeting neighborhood friends, or simply enjoying peaceful naps in favorite spots. Their playful spirit and gentle heart made them the perfect family member, always ready to offer comfort when needed and celebrate life's happy moments. This special pet showed everyone that true friendship knows no bounds and that every day is better when shared with someone who loves you unconditionally."
        };
        
        // Use description hash for consistent responses
        int index = Math.abs(description.hashCode() % storyTemplates.length);
        return storyTemplates[index];
    }
}
