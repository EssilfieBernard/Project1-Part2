package com.s3rds.ImageToS3RDS;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final String bucketName = "essilfie";

    private final AmazonS3 amazonS3;

    public String uploadImage(MultipartFile file, String description) throws IOException {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        var userMetaData = new HashMap<String, String>();
        userMetaData.put("description", description);
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        metadata.setUserMetadata(userMetaData);

        amazonS3.putObject(new PutObjectRequest(
                bucketName,
                fileName,
                file.getInputStream(),
                metadata
        ));

        return amazonS3.getUrl(bucketName, fileName).toString();
    }

    public void deleteImage(String fileName) {
        amazonS3.deleteObject(bucketName, fileName);
    }
}
