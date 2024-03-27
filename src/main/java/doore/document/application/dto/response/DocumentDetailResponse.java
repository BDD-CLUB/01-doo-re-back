package doore.document.application.dto.response;

import doore.document.domain.DocumentAccessType;
import doore.document.domain.DocumentType;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

public record DocumentDetailResponse(
        Long id,
        String title,
        String description,
        DocumentAccessType accessType,
        DocumentType type,
        List<FileResponse> files,
        LocalDate date,
        String uploader
) {
    @Builder
    public DocumentDetailResponse(Long id, String title, String description, DocumentAccessType accessType,
                                  DocumentType type, List<FileResponse> files, LocalDate date, String uploader) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.accessType = accessType;
        this.type = type;
        this.files = files;
        this.date = date;
        this.uploader = uploader;
    }
}
