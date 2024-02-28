package doore.document.application.dto.response;

import java.time.LocalDate;

public record DocumentCondensedResponse(
        Long id,
        String title,
        String description,
//        String thumbnailUrl,
        LocalDate date,
        Long uploaderId
) {
}
