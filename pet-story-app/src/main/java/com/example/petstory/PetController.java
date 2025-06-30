package com.example.petstory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
    @ResponseBody
    public String index() {
        return "<html><body>" +
                "<h1>Upload your pet picture</h1>" +
                "<form method='POST' enctype='multipart/form-data' action='/upload'>" +
                "<input type='file' name='image' accept='image/*'/>" +
                "<button type='submit'>Upload</button>" +
                "</form></body></html>";
    }

    @PostMapping("/upload")
    @ResponseBody
    public String handleUpload(@RequestParam("image") MultipartFile file) throws Exception {
        Path tempFile = Files.createTempFile("upload", file.getOriginalFilename());
        file.transferTo(tempFile.toFile());
        String caption = imageService.caption(tempFile.toFile());
        String story = storyService.generateStory(caption);
        Files.deleteIfExists(tempFile);
        return "<html><body>" +
                "<p><strong>Description:</strong> " + caption + "</p>" +
                "<p><strong>Story:</strong> " + story + "</p>" +
                "</body></html>";
    }
}
