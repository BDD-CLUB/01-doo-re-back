package doore.document.application.dto.response;

import doore.document.domain.DocumentAccessType;
import doore.document.domain.DocumentType;
import doore.member.domain.Member;
import java.time.LocalDate;
import java.util.List;

public record DocumentDetailResponse(
        Long id,
        String title,
        String description,
        DocumentAccessType accessType,
        DocumentType type,
        List<FileResponse> files,
        LocalDate date,
        Long uploaderId
) {
}
