package doore.garden.application.dto.response;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;
import lombok.Builder;

public record DayGardenResponse(
        LocalDate contributeDate,
        int contributeCount
) {
    @Builder
    public DayGardenResponse(final LocalDate contributeDate, final int contributeCount) {
        this.contributeDate = contributeDate;
        this.contributeCount = contributeCount;
    }

    public static DayGardenResponse of(final LocalDate date, final int contributeNumber) {
        return DayGardenResponse.builder()
                .contributeDate(date)
                .contributeCount(contributeNumber)
                .build();
    }
}
