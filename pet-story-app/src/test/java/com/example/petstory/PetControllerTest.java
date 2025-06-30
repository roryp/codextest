package com.example.petstory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PetController.class)
class PetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImageService imageService;

    @MockBean
    private StoryService storyService;

    @Test
    void indexPageShouldReturnSuccessfully() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void uploadValidImageShouldReturnResult() throws Exception {
        // Arrange
        when(imageService.caption(any(File.class))).thenReturn("A cute dog playing in the park");
        when(storyService.generateStory(anyString())).thenReturn("Once upon a time, there was a playful dog...");

        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        // Act & Assert
        mockMvc.perform(multipart("/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(view().name("result"))
                .andExpect(model().attributeExists("caption"))
                .andExpect(model().attributeExists("story"))
                .andExpect(model().attributeExists("fileName"));
    }

    @Test
    void uploadEmptyFileShouldRedirectWithError() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "image",
                "empty.jpg",
                "image/jpeg",
                new byte[0]
        );

        mockMvc.perform(multipart("/upload").file(emptyFile))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    void uploadInvalidFileTypeShouldRedirectWithError() throws Exception {
        MockMultipartFile invalidFile = new MockMultipartFile(
                "image",
                "document.txt",
                "text/plain",
                "not an image".getBytes()
        );

        mockMvc.perform(multipart("/upload").file(invalidFile))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeExists("error"));
    }
}