package com.thoughtNest.backend.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@Service
public class GCSUploadService {

    @Value("${gcs.bucket.name}")
    private String bucketName;

    @Value("${gcs.credentials.file}")
    private String credentialsPath;

    public String uploadFile(String originalFilename, InputStream inputStream, String contentType) throws IOException {
    String fileName = UUID.randomUUID() + "-" + originalFilename;

    // Load credentials from classpath
    InputStream credentialsStream = getClass().getClassLoader().getResourceAsStream(credentialsPath);
    if (credentialsStream == null) {
        throw new IOException("GCS credentials file not found in classpath: " + credentialsPath);
    }

    Storage storage = StorageOptions.newBuilder()
            .setCredentials(ServiceAccountCredentials.fromStream(credentialsStream))
            .build()
            .getService();

    BlobId blobId = BlobId.of(bucketName, fileName);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
            .setContentType(contentType)
            .setAcl(java.util.List.of(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER))) // Public ACL
            .build();

    storage.create(blobInfo, inputStream);

    return String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
}

}
