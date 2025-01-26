package doore.file.api;

import doore.file.application.S3DocumentFileService;
import doore.file.application.S3ImageFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {
    private final S3ImageFileService s3ImageFileService;
    private final S3DocumentFileService s3DocumentFileService;

    @GetMapping("/images/{uuid}")
    public ResponseEntity<Void> getImageUrl(@PathVariable String uuid) {
        final String imageUrl = s3ImageFileService.getS3Url(uuid);

        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                .header("Location", imageUrl)
                .build();
    }

    @GetMapping("/documents/{uuid}")
    public ResponseEntity<Void> getDocumentUrl(@PathVariable String uuid) {
        final String presignedUrl = s3DocumentFileService.generatePresignedUrl(uuid);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", presignedUrl)
                .build();
    }
}
