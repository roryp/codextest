package com.example.petstory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

@Controller
public class PetController {

    private final ImageService imageService;
    private final StoryService storyService;

    public PetController(ImageService imageService, StoryService storyService) {
        this.imageService = imageService;
        this.storyService = storyService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/upload")
    public String handleUpload(@RequestParam("image") MultipartFile file, Model model) throws Exception {
        Path tempFile = Files.createTempFile("upload", file.getOriginalFilename());
        file.transferTo(tempFile.toFile());
        String caption = imageService.caption(tempFile.toFile());
        String story = storyService.generateStory(caption);
        Files.deleteIfExists(tempFile);
        model.addAttribute("caption", caption);
        model.addAttribute("story", story);
        return "result";
    }
}
