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
    public DayGardenResponse(int dayOfYear, int dayOfWeek, int weekOfYear, int contributeCount) {
        this.dayOfYear = dayOfYear;
        this.dayOfWeek = dayOfWeek;
        this.weekOfYear = weekOfYear;
        this.contributeCount = contributeCount;
    }

    public static DayGardenResponse of(LocalDate date, int contributeNumber) {
        int weekOfYear = getWeekOfYear(date)-1;
        int dayOfWeek = date.getDayOfWeek().getValue() - 1;
        return DayGardenResponse.builder()
                .dayOfYear(date.getDayOfYear() - 1)
                .weekOfYear(weekOfYear)
                .dayOfWeek(dayOfWeek)
                .contributeCount(contributeNumber)
                .build();
    }

    private static int getWeekOfYear(LocalDate date) {
        WeekFields weekFields = WeekFields.of(Locale.KOREA);
        return date.get(weekFields.weekOfWeekBasedYear());
    }
}
