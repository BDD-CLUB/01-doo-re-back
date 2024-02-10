package doore.file.application;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public abstract class S3FileService {

    protected final String EXTENSION_DELIMITER = ".";

    protected final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket}")
    protected String bucket;

    abstract String upload(final MultipartFile file);

    protected String getFileExtension(final MultipartFile file) {
        final String originalFileName = file.getOriginalFilename();
        final int extensionIndex = Objects.requireNonNull(originalFileName)
                .lastIndexOf(EXTENSION_DELIMITER);
        validateExtension(extensionIndex, originalFileName);
        return originalFileName.substring(extensionIndex);
    }

    abstract void validateExtension(int extensionIndex, String originalFileName);

    protected ObjectMetadata getObjectMetadata(final MultipartFile multipartFile) {
        final ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());
        return metadata;
    }

    protected String createFileName(final String fileExtension) {
        return getFilePath() + UUID.randomUUID().toString().concat(fileExtension);
    }

    abstract String getFilePath();

    public void deleteFile(final String fileName) {
        final String decodedFileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8);
        amazonS3.deleteObject(bucket, decodedFileName);
    }
}
