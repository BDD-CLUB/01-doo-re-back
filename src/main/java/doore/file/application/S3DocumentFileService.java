package doore.file.application;

import static doore.file.exception.FileExceptionType.FAIL_UPLOAD_DOCUMENT_FILE;
import static doore.file.exception.FileExceptionType.INVALID_IMAGE_FILE_FORMAT;

import com.amazonaws.services.s3.AmazonS3;
import doore.file.exception.FileException;
import doore.file.exception.FileExceptionType;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class S3DocumentFileService extends S3FileService {

    private static final List<String> DOCUMENT_MIME_TYPES = List.of(
            "text/plain", "application/zip", //txt, zip
            "application/pdf", "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation", //pdf, ppt, pptx
            "video/mp4", "video/x-msvideo", "video/webm", //mp4, avi, webm
            "audio/mpeg", "audio/wav", "audio/webm",// mp3, wav, webm
            "image/jpeg", "image/png", "image/gif", "image/webp" //jpeg, jpg, png, gif, webp
    );

    @Value("${aws.s3.folder.documentFolder}")
    private String documentFolder;

    public S3DocumentFileService(final AmazonS3 amazonS3) {
        super(amazonS3);
    }

    @Override
    protected FileExceptionType failUploadErrorMessage() {
        return FAIL_UPLOAD_DOCUMENT_FILE;
    }

    @Override
    void validateExtension(String mimeType) {
        final boolean isValidate = DOCUMENT_MIME_TYPES.stream()
                .anyMatch(validType -> validType.equalsIgnoreCase(mimeType));
        if (!isValidate) {
            throw new FileException(INVALID_IMAGE_FILE_FORMAT);
        }
    }

    @Override
    String getFileFolder() {
        return documentFolder;
    }
}
