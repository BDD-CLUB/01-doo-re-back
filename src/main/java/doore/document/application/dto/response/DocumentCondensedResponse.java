package doore.document.application.dto.response;

import doore.document.domain.Document;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record DocumentCondensedResponse(
        Long id,
        String title,
        String description,
        LocalDate date,
        String uploaderName
) {
    public static DocumentCondensedResponse of(final Document document, final String uploaderName) {
        return DocumentCondensedResponse.builder()
                .id(document.getId())
                .title(document.getName())
                .description(document.getDescription())
                .date(document.getCreatedAt().toLocalDate())
                .uploaderName(uploaderName)
                .build();
    }
}
