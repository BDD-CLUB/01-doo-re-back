package doore.garden.application.dto.response;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;
import lombok.Builder;

public record DayGardenResponse(
        int dayOfYear,
        int dayOfWeek,
        int weekOfYear,
        int contributeCount
) {
    @Builder
    public DayGardenResponse(final int dayOfYear, final int dayOfWeek, final int weekOfYear, final int contributeCount) {
        this.dayOfYear = dayOfYear;
        this.dayOfWeek = dayOfWeek;
        this.weekOfYear = weekOfYear;
        this.contributeCount = contributeCount;
    }

    public static DayGardenResponse of(final LocalDate date, final int contributeNumber) {
        final int weekOfYear = getWeekOfYear(date)-1;
        final int dayOfWeek = date.getDayOfWeek().getValue() - 1;
        return DayGardenResponse.builder()
                .dayOfYear(date.getDayOfYear() - 1)
                .weekOfYear(weekOfYear)
                .dayOfWeek(dayOfWeek)
                .contributeCount(contributeNumber)
                .build();
    }

    private static int getWeekOfYear(final LocalDate date) {
        final WeekFields weekFields = WeekFields.of(Locale.KOREA);
        return date.get(weekFields.weekOfWeekBasedYear());
    }
}
