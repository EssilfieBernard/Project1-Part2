package com.s3rds.ImageToS3RDS;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageMetadataRepository repository;
    private final S3Service s3Service;



    @GetMapping
    public Page<ImageMetadata> getAllImages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return repository.findAllByOrderByUploadedAtDesc(PageRequest.of(page, size));
    }

    @PostMapping
    public ImageMetadata uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("description") String description) throws IOException {

        String url = s3Service.uploadImage(file, description);

        ImageMetadata metadata = ImageMetadata.builder()
                .url(url)
                .description(description)
                .uploadedAt(LocalDateTime.now())
                .build();

        return repository.save(metadata);
    }
}
