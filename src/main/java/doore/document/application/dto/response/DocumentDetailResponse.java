package doore.document.application.dto.response;

import doore.document.domain.Document;
import doore.document.domain.DocumentAccessType;
import doore.document.domain.DocumentType;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record DocumentDetailResponse(
        Long id,
        String title,
        String description,
        DocumentAccessType accessType,
        DocumentType type,
        List<FileResponse> files,
        LocalDate date,
        String uploaderName
) {
    public static DocumentDetailResponse of(final Document document, final List<FileResponse> fileResponses,
                                            final String uploaderName) {
        return DocumentDetailResponse
                .builder()
                .id(document.getId())
                .title(document.getName())
                .description(document.getDescription())
                .accessType(document.getAccessType())
                .type(document.getType())
                .files(fileResponses)
                .date(document.getCreatedAt().toLocalDate())
                .uploaderName(uploaderName)
                .build();
    }
}
