package com.thoughtNest.backend.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.cloud.storage.Acl;
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

        // Use default credentials provided by Google Cloud Run
        Storage storage = StorageOptions.getDefaultInstance().getService();

        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(contentType)
                .setAcl(java.util.List.of(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER))) // Make public
                .build();

        storage.create(blobInfo, inputStream);

        return String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
    }
}
