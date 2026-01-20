package com.example.studyE.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final Cloudinary cloudinary;

    private static final String BASE_FOLDER = "Home/studyE";


    public String upload(MultipartFile file, String type) throws IOException {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("File is empty");

        final String t = (type == null || type.isBlank()) ? "auto" : type.trim().toLowerCase();

        final String folder = switch (t) {
            case "image" -> BASE_FOLDER + "/lessons/images";
            case "video", "audio" -> BASE_FOLDER + "/lessons/media";
            default -> BASE_FOLDER + "/lessons/uploads";
        };

        Map<String, Object> options = new HashMap<>();
        options.put("folder", folder);

        if (t.equals("video") || t.equals("audio")) {
            options.put("resource_type", "video");
        } else if (t.equals("image")) {
            options.put("resource_type", "image");
        } else {
            options.put("resource_type", "auto");
        }

        options.put("unique_filename", true);
        options.put("overwrite", false);

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), options);

        Object secureUrl = uploadResult.get("secure_url");
        if (secureUrl == null) throw new RuntimeException("Cloudinary missing secure_url");

        return secureUrl.toString();
    }

    public String uploadImage(MultipartFile file) throws IOException {
        return upload(file, "image");
    }

    public List<String> uploadMultipleImages(MultipartFile[] files) throws IOException {
        List<String> urls = new ArrayList<>();
        if (files == null) return urls;

        for (MultipartFile f : files) {
            if (f == null || f.isEmpty()) continue;
            urls.add(upload(f, "image"));
        }
        return urls;
    }
}
