package com.microservice.movie.application.services;

import com.microservice.movie.application.ports.in.MovieServicePort;
import com.microservice.movie.application.ports.in.UploadMoviePort;
import com.microservice.movie.application.ports.out.FileStoragePort;
import com.microservice.movie.domain.model.Movie;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UploadMovieServiceImpl implements UploadMoviePort {
    private final FileStoragePort fileStoragePort;
    private final MovieServicePort movieServicePort;
    @Value("${aws.region}")
    private String region;

    @Value("${aws.bucket}")
    private String bucket;

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Async
    @Override
    public void processAndUploadMovieAsync(
            Path videoPath,
            Path audioEnPath,
            Path audioEsPath,
            Path subsEnPath,
            Path subsEsPath,
            Path imagePath,
            Movie movie) {

        try {
            String baseName = movie.getTitle().replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();
            Path tempDir = Files.createTempDirectory("processing_" + baseName);

            // Copy main file to tempDir
            Path originalFile = tempDir.resolve(baseName + ".mp4");
            Files.copy(videoPath, originalFile, StandardCopyOption.REPLACE_EXISTING);

            // Generate qualities (uses FFmpeg)
            generateQualities(originalFile.toString(), tempDir.toString(), baseName);

            String BaseFolder;
            if (Objects.equals(movie.getIdCategory(), 2L)) {
                BaseFolder = "series/" + baseName
                        + "/season" + movie.getSeasonNumber()
                        + "/episode" + movie.getEpisodeNumber();
            } else {
                BaseFolder = baseName;
            }

            Map<String, String> urls = new HashMap<>();
            for (String quality : List.of("1080p", "720p", "480p")) {
                Path path = tempDir.resolve(baseName + "_" + quality + ".mp4");
                String s3Key = BaseFolder + "/" + quality + "/video.mp4";
                fileStoragePort.uploadFile(bucket, s3Key, path);
                urls.put(quality, s3Key);
            }

            // Audios
            String audioEnKey = BaseFolder + "/audio/audio_en.m4a";
            fileStoragePort.uploadFile(bucket, audioEnKey, audioEnPath);

            String audioEsKey = BaseFolder + "/audio/audio_es.m4a";
            fileStoragePort.uploadFile(bucket, audioEsKey, audioEsPath);

            // Subtitles
            String subsEnKey = BaseFolder + "/subs/subs_en.vtt";
            fileStoragePort.uploadFile(bucket, subsEnKey, subsEnPath);

            String subsEsKey = BaseFolder + "/subs/subs_es.vtt";
            fileStoragePort.uploadFile(bucket, subsEsKey, subsEsPath);

            // Image
            String imageKey = BaseFolder + "/banner/banner.jpg";
            fileStoragePort.uploadFile(bucket, imageKey, imagePath);

            // Update Movie fields
            movie.setAudioUrlEn(audioEnKey);
            movie.setAudioUrlEs(audioEsKey);
            movie.setSubTitlesEnglish(subsEnKey);
            movie.setSubTitlesSpanish(subsEsKey);
            movie.setImageBanner(imageKey);

            movie.setVideoUrl1080p(urls.get("1080p"));
            movie.setVideoUrl720p(urls.get("720p"));
            movie.setVideoUrl480p(urls.get("480p"));
            movie.setEstate(true);

            // Save back to the database with updated URLs
            movieServicePort.save(movie);

            // Cleaning
            deleteDirectory(tempDir.toFile());

        } catch (Exception e) {
            // Error log
            logger.error("Error processing and uploading async movie", e);
        }
    }

    private void generateQualities(String inputPath, String outputDir, String baseName) throws IOException, InterruptedException {
        Map<String, String> resolutions = Map.of(
                "1080p", "1920:1080",
                "720p", "1280:720",
                "480p", "854:480"
        );

        Map<String, String> crfByQuality = Map.of(
                "1080p", "28",
                "720p", "30",
                "480p", "32"
        );

        Map<String, String> presetByQuality= Map.of(
                "1080p", "fast",
                "720p", "fast",
                "480p", "fast"
        );

        for (String calidad : resolutions.keySet()) {
            String escala = resolutions.get(calidad);
            String crf = crfByQuality.get(calidad);
            String preset = presetByQuality.get(calidad);
            String outputPath = outputDir + "/" + baseName + "_" + calidad + ".mp4";
            ProcessBuilder builder = new ProcessBuilder(
                    "ffmpeg", "-i", inputPath,
                    "-vf", "scale=" + escala,
                    "-an",
                    "-c:v", "libx264",
                    "-crf", crf,
                    "-preset", preset,
                    outputPath
            );

            builder.redirectErrorStream(true);
            Process process = builder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                while ((reader.readLine()) != null) {

                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Error encoding " + calidad);
            }

        }
    }

    private void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                deleteDirectory(file);
            }
        }
        dir.delete();
    }
}
