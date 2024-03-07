package doore.file.application;

import static doore.file.exception.FileExceptionType.FAIL_UPLOAD_IMAGE_FILE;
import static doore.file.exception.FileExceptionType.INVALID_IMAGE_FILE_FORMAT;

import com.amazonaws.services.s3.AmazonS3;
import doore.file.exception.FileException;
import doore.file.exception.FileExceptionType;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class S3ImageFileService extends S3FileService {

    private static final List<String> IMAGE_FILE_EXTENSIONS = List.of("image/jpeg", "image/png", "image/gif", "image/webp");

    @Value("${aws.s3.folder.imageFolder}")
    private String imageFolder;

    public S3ImageFileService(final AmazonS3 amazonS3) {
        super(amazonS3);
    }

    @Override
    protected FileExceptionType failUploadErrorMessage() {
        return FAIL_UPLOAD_IMAGE_FILE;
    }

    @Override
    public void validateExtension(final String mimeType) {
        final boolean isValidate = IMAGE_FILE_EXTENSIONS.stream()
                .anyMatch(validType -> validType.equalsIgnoreCase(mimeType));
        if (!isValidate) {
            throw new FileException(INVALID_IMAGE_FILE_FORMAT);
        }
    }

    @Override
    public String getFileFolder() {
        return imageFolder;
    }
}
