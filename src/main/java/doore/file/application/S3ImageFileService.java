package doore.file.application;

import static doore.file.exception.FileExceptionType.FAIL_UPLOAD_IMAGE_FILE;
import static doore.file.exception.FileExceptionType.FILE_IS_NULL;
import static doore.file.exception.FileExceptionType.INVALID_IMAGE_FILE_FORMAT;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import doore.file.exception.FileException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class S3ImageFileService extends S3FileService {

    private static final List<String> IMAGE_FILE_EXTENSIONS = List.of(".jpg", ".png", ".jpeg", ".gif", ".webp");

    @Value("${aws.s3.folder.imageFolder}")
    private String imageFolder;

    public S3ImageFileService(final AmazonS3 amazonS3) {
        super(amazonS3);
    }

    @Override
    public String upload(final MultipartFile file) {
        validateNotNull(file);

        final String fileExtension = getFileExtension(file);
        final String newFileName = createFileName(fileExtension);
        final ObjectMetadata objectMetadata = getObjectMetadata(file);

        try (final InputStream inputStream = file.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucket, newFileName, inputStream, objectMetadata));
        } catch (final IOException exception) {
            throw new FileException(FAIL_UPLOAD_IMAGE_FILE);
        }
        return newFileName;
    }

    private void validateNotNull(final MultipartFile file) {
        if (file == null) {
            throw new FileException(FILE_IS_NULL);
        }
    }

    @Override
    public void validateExtension(final int extensionIndex, final String originalFileName) {
        final boolean isValidate = IMAGE_FILE_EXTENSIONS.stream()
                .anyMatch(e -> e.equalsIgnoreCase(originalFileName.substring(extensionIndex)));
        if (!isValidate) {
            throw new FileException(INVALID_IMAGE_FILE_FORMAT);
        }
    }

    @Override
    public String getFileFolder() {
        return imageFolder;
    }
}
