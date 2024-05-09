package doore.garden.application.dto.response;

import lombok.Builder;

public record DayGardenResponse(
        int dayOfYear,
        int dayOfWeek,
        int weekOfYear,
        int attributeNumber
) {
    @Builder
    public DayGardenResponse(int dayOfYear, int dayOfWeek, int weekOfYear, int attributeNumber) {
        this.dayOfYear = dayOfYear;
        this.dayOfWeek = dayOfWeek;
        this.weekOfYear = weekOfYear;
        this.attributeNumber = attributeNumber;
    }
}
