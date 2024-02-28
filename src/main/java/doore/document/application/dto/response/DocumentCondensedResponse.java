package doore.document.application.dto.response;

import doore.member.domain.Member;
import java.time.LocalDate;

public record DocumentCondensedResponse(
        Long id,
        String title,
        String description,
//        String thumbnailUrl,
        LocalDate date,
        Member uploader
) {
}
