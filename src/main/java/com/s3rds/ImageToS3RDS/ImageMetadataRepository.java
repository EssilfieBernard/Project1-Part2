package com.s3rds.ImageToS3RDS;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageMetadataRepository extends JpaRepository<ImageMetadata, Integer> {
    Page<ImageMetadata> findAllByOrderByUploadedAtDesc(Pageable pageable);
}
