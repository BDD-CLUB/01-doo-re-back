package doore.file.application;

import static doore.file.exception.FileExceptionType.INVALID_IMAGE_FILE_FORMAT;
import static doore.file.exception.FileExceptionType.UPLOAD_IMAGE_FILE_FAIL;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import doore.file.domain.FileType;
import doore.file.exception.FileException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class S3FileService {

    private static final String EXTENSION_DELIMITER = ".";
    private static final List<String> IMAGE_FILE_EXTENSIONS = List.of(".jpg", ".png", ".jpeg", ".gif");

    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.folder.imageFolder}")
    private String imageFolder;

    @Value("${aws.s3.folder.documentFolder}")
    private String documentFolder;

    public String uploadFile(final MultipartFile file, final FileType fileType) {
        if (file == null) {
            return "";
        }

        switch (fileType) {
            case IMAGE -> {
                return uploadImage(file, fileType);
            }
            case DOCUMENT -> {
                // TODO: 2/2/24 학습자료 파일 업로드 기능 구현
            }
        }
        throw new IllegalArgumentException("해당하는 파일의 종류가 없습니다.");
    }

    public String uploadImage(final MultipartFile file, final FileType fileType) {
        final String fileExtension = getFileExtension(file);
        final String newFileName = createFileName(fileExtension, fileType);
        final ObjectMetadata objectMetadata = getObjectMetadata(file);

        try (final InputStream inputStream = file.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucket, newFileName, inputStream, objectMetadata));
        } catch (final IOException exception) {
            throw new FileException(UPLOAD_IMAGE_FILE_FAIL);
        }
        return newFileName;
    }

    private String getFileExtension(final MultipartFile file) {
        final String originalFileName = file.getOriginalFilename();
        final int extensionIndex = Objects.requireNonNull(originalFileName)
                .lastIndexOf(EXTENSION_DELIMITER);
        validateImageExtension(extensionIndex, originalFileName);
        return originalFileName.substring(extensionIndex);
    }

    private void validateImageExtension(final int extensionIndex, final String originalFileName) {
        final boolean isValidate = IMAGE_FILE_EXTENSIONS.stream()
                .anyMatch(e -> e.equalsIgnoreCase(originalFileName.substring(extensionIndex)));
        if (isValidate) {
            return;
        }
        throw new FileException(INVALID_IMAGE_FILE_FORMAT);
    }

    private ObjectMetadata getObjectMetadata(final MultipartFile multipartFile) {
        final ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());
        return metadata;
    }

    private String createFileName(final String fileExtension, final FileType fileType) {
        return getFileFolder(fileType) + UUID.randomUUID().toString().concat(fileExtension);
    }

    private String getFileFolder(final FileType fileType) {
        switch (fileType) {
            case IMAGE -> {
                return imageFolder;
            }
            case DOCUMENT -> {
                return documentFolder;
            }
        }
        throw new IllegalArgumentException("해당하는 파일의 종류가 없습니다.");
    }

    public void deleteFile(final String fileName) {
        final String decodedFileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8);
        amazonS3.deleteObject(bucket, decodedFileName);
    }
}
