package doore.file.application;

import com.amazonaws.services.s3.AmazonS3;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class S3DocumentFileService extends S3FileService {

    @Value("${aws.s3.path.documentPath}")
    private String documentPath;

    public S3DocumentFileService(final AmazonS3 amazonS3) {
        super(amazonS3);
    }

    @Override
    String upload(final MultipartFile file) {
        return null; // TODO: 2/10/24 구현
    }

    @Override
    void validateExtension(final int extensionIndex, final String originalFileName) {
        // TODO: 2/10/24 구현
    }

    @Override
    String getFilePath() {
        return documentPath;
    }
}
