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
        if (imageBase64.startsWith("http")) {
            log.info("Can't save image on MinIO because the image is on http format: {}", imageBase64);
            throw new IndexerException(String.format("Can't save image on MinIO because the image is on http format: %s", imageBase64));
        }

        log.info("Saving image on MinIO with name {}", name);

        byte[] imageBytes;
        try {
            imageBytes = Base64.getDecoder().decode(imageBase64);
        } catch (IllegalArgumentException e) {
            log.error("Can't decode this image {}", imageBase64, e);
            throw new IndexerException(String.format("Can't decode this image %s", imageBase64), e);
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
