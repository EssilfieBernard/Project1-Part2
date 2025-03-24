package com.s3rds.ImageToS3RDS;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageMetadataRepository extends JpaRepository<ImageMetadata, Integer> {
    Page<ImageMetadata> findAllByOrderByUploadedAtDesc(Pageable pageable);
    Optional<ImageMetadata> findByUrl(String url);
}
