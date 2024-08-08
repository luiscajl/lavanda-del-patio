package es.lavanda.indexers.service;

import es.lavanda.indexers.exception.IndexerException;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.Base64;

import es.lavanda.indexers.exception.IndexerException;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    @Value("${minio.endpoint}")
    private String minioEndpoint;

    @Value("${minio.access.key}")
    private String minioAccessKey;

    @Value("${minio.access.secret}")
    private String minioSecretKey;

    @Value("${minio.bucket.name}")
    private String minioBucketName;

    private MinioClient minioClient;


    @PostConstruct
    public void init() {
        minioClient = MinioClient.builder()
                .endpoint(minioEndpoint)
                .credentials(minioAccessKey, minioSecretKey)
                .build();
    }

    public String saveImage(String name, String imageBase64) {
        byte[] imageBytes;

        if (imageBase64.startsWith("http") && imageBase64.contains(" ")) {
            log.info("Downloading image from URL: {}", imageBase64);
            try {
                imageBytes = downloadImageFromUrl(imageBase64);
            } catch (Exception e) {
                log.error("Can't download image from URL {}", imageBase64, e);
                throw new IndexerException(String.format("Can't download image from URL %s", imageBase64), e);
            }
        } else if (!imageBase64.startsWith("http")) {
            log.info("Saving image on MinIO with name {}", name);
            try {
                imageBytes = Base64.getDecoder().decode(imageBase64);
            } catch (IllegalArgumentException e) {
                log.error("Can't decode this image {}", imageBase64, e);
                throw new IndexerException(String.format("Can't decode this image %s", imageBase64), e);
            }
        } else {
            log.error("Image is http without spaces and is not needed to upload: {}", imageBase64);
            return imageBase64;
        }

        String objectName = name.replaceAll(" ", "_") + ".jpg";
        if (objectExistsInMinio(objectName)) {
            log.info("Image already exists in MinIO, skipping upload {}", objectName);
        } else {
            if (uploadImageToMinio(objectName, imageBytes)) {
                log.info("Image uploaded successfully to MinIO {}", objectName);
            } else {
                log.error("Failed to upload image to MinIO {}", objectName);
                throw new IndexerException("Failed to upload image to MinIO");
            }
        }
        return getImageUrl(objectName);
    }

    private byte[] downloadImageFromUrl(String imageUrl) throws Exception {
        URL url = new URL(imageUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.connect();
        try (InputStream inputStream = connection.getInputStream()) {
            return StreamUtils.copyToByteArray(inputStream);
        }
    }

    private boolean objectExistsInMinio(String objectName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(minioBucketName)
                            .object(objectName)
                            .build()
            );
            return true;
        } catch (Exception e) {
            log.error("Error checking if object exists in MinIO", e);
            return false;
        }
    }


    private boolean uploadImageToMinio(String objectName, byte[] imageBytes) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioBucketName)
                            .object(objectName)
                            .stream(bais, bais.available(), -1)
                            .contentType("image/jpeg")
                            .build()
            );
            return true;
        } catch (Exception e) {
            log.error("Error uploading image to MinIO", e);
            return false;
        }
    }

    private String getImageUrl(String objectName) {
        String imageUrl = "https://" + minioEndpoint + "/" + minioBucketName + "/" + objectName;
        log.info("Get image {} with this URL {}", objectName, imageUrl);
        return imageUrl;
    }

}
