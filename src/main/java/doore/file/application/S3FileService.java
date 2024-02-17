package doore.file.application;

import static doore.file.exception.FileExceptionType.FILE_IS_NULL;
import static doore.file.exception.FileExceptionType.INVALID_FILE_ACCESS;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import doore.file.exception.FileException;
import doore.file.exception.FileExceptionType;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
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

    public String upload(final MultipartFile file) {
        validateNotNull(file);

        final String fileExtension = getFileExtension(file);
        final String newFileName = createFileName(fileExtension);
        final ObjectMetadata objectMetadata = getObjectMetadata(file);

        try (final InputStream inputStream = file.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucket, newFileName, inputStream, objectMetadata));
        } catch (final IOException exception) {
            throw new FileException(FailUploadErrorMessage());
        }
        return newFileName;
    }

    abstract FileExceptionType FailUploadErrorMessage();

    protected String getFileExtension(final MultipartFile file) {
        final String originalFileName = file.getOriginalFilename();
        final int extensionIndex = Objects.requireNonNull(originalFileName)
                .lastIndexOf(EXTENSION_DELIMITER);

        try {
            String mimeType = new Tika().detect(file.getInputStream());
            validateExtension(mimeType);
        } catch (IOException e) {
            throw new FileException(INVALID_FILE_ACCESS);
        }
        return originalFileName.substring(extensionIndex);
    }

    abstract void validateExtension(String mimeType);

    protected ObjectMetadata getObjectMetadata(final MultipartFile multipartFile) {
        final ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());
        return metadata;
    }

    protected String createFileName(final String fileExtension) {
        return getFileFolder() + UUID.randomUUID().toString().concat(fileExtension);
    }

    abstract String getFileFolder();

    protected void validateNotNull(final MultipartFile file) {
        if (file == null) {
            throw new FileException(FILE_IS_NULL);
        }
    }

    public void deleteFile(final String fileName) {
        final String decodedFileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8);
        amazonS3.deleteObject(bucket, decodedFileName);
    }
}
