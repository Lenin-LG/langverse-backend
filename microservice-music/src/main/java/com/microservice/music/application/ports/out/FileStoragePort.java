package com.microservice.music.application.ports.out;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;

public interface FileStoragePort {
    boolean uploadFile( String key, Path file);
    void downloadFile(String bucket, String key) throws IOException;
    String generatePresignedUploadUrl( String key, Duration duration);
    String generatePresignedDownloadUrl( String key);
}