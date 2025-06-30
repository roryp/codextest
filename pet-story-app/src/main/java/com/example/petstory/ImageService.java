package com.example.petstory;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Service
public class ImageService {

    public String caption(File image) throws Exception {
        ProcessBuilder pb = new ProcessBuilder("python3", "scripts/caption.py", image.getAbsolutePath());
        pb.directory(new File(".."));
        Process process = pb.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String caption = reader.readLine();
            process.waitFor();
            return caption;
        }
    }
}
