package doore.garden.application.dto.response;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record DayGardenResponse(
        LocalDate contributeDate,
        int contributeCount
) {
    public static DayGardenResponse of(final LocalDate date, final int contributeNumber) {
        return DayGardenResponse.builder()
                .contributeDate(date)
                .contributeCount(contributeNumber)
                .build();
    }
}
