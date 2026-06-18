package com.pakbzer.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.pakbzer.config.GcpStorageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Service
public class StorageService {

    private static final Logger log = LoggerFactory.getLogger(StorageService.class);

    private final GcpStorageProperties properties;
    private volatile Storage storageClient;

    public StorageService(GcpStorageProperties properties) {
        this.properties = properties;
    }

    public boolean isEnabled() {
        return properties.isConfigured();
    }

    /**
     * Uploads a local file to GCS and returns its public URL, if storage is configured.
     */
    public Optional<String> uploadFile(Path localFile, String objectName, String contentType) {
        if (!isEnabled()) {
            return Optional.empty();
        }

        try {
            Storage client = getClient();
            String bucket = properties.getStorage().getBucket();
            BlobId blobId = BlobId.of(bucket, objectName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(contentType)
                    .build();
            client.create(blobInfo, Files.readAllBytes(localFile));
            return Optional.of(String.format("https://storage.googleapis.com/%s/%s", bucket, objectName));
        } catch (IOException ex) {
            log.warn("GCS upload failed for {}: {}", objectName, ex.getMessage());
            return Optional.empty();
        }
    }

    private Storage getClient() {
        if (storageClient == null) {
            synchronized (this) {
                if (storageClient == null) {
                    storageClient = StorageOptions.newBuilder()
                            .setProjectId(properties.getProjectId())
                            .build()
                            .getService();
                }
            }
        }
        return storageClient;
    }
}
