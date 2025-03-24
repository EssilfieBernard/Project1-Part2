package com.s3rds.ImageToS3RDS;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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

    @DeleteMapping("{id}")
    public void deleteImage(@PathVariable Integer id) {
        var metadata = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));
        String fileName = metadata.getUrl().substring(metadata.getUrl().lastIndexOf("/") + 1);
        s3Service.deleteImage(fileName);
        repository.deleteById(id);
    }
}
