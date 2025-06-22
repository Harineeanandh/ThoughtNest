package com.thoughtNest.backend.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@Service
public class GCSUploadService {

    @Value("${gcs.bucket.name}")
    private String bucketName;

    public String uploadFile(String originalFilename, InputStream inputStream, String contentType) throws IOException {
        String fileName = UUID.randomUUID() + "-" + originalFilename;
        System.out.println("Starting GCS upload: " + fileName);

        try {
            Storage storage = StorageOptions.getDefaultInstance().getService();
            System.out.println("GCS Storage object initialized");

            BlobId blobId = BlobId.of(bucketName, fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(contentType)
                .build();

            storage.create(blobInfo, inputStream);
            System.out.println("File uploaded to bucket: " + bucketName);

            String publicUrl = String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
            System.out.println("Public URL: " + publicUrl);
            return publicUrl;
        } catch (Exception e) {
            System.err.println("GCS Upload failed:");
            e.printStackTrace();
            throw new IOException("GCS Upload failed: " + e.getMessage(), e);
        }
    }
}
