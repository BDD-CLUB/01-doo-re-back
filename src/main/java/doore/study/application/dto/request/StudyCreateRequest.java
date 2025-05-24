package doore.study.application.dto.request;

import static doore.study.domain.StudyStatus.IN_PROGRESS;
import static doore.study.domain.StudyStatus.UPCOMING;

import com.fasterxml.jackson.annotation.JsonFormat;
import doore.study.domain.Study;
import doore.study.domain.StudyStatus;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record StudyCreateRequest(
        @NotNull(message = "이름을 입력해주세요.")
        String name,

        @NotNull(message = "설명을 입력해주세요.")
        String description,

        @NotNull(message = "시작 날짜를 입력해주세요.")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,

        @Nullable
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate endDate,

        @NotNull(message = "작물을 골라주세요.")
        Long cropId
) {
    public StudyCreateRequest(final String name, final String description, final LocalDate startDate, final LocalDate endDate,
                              final Long cropId) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.cropId = cropId;
    }

    public Study toStudy(final Long teamId) {
        final LocalDate now = LocalDate.now();
        final StudyStatus status;

        if (now.isBefore(this.startDate)) status = UPCOMING;
        else if (this.endDate != null
                && (!now.isBefore(this.startDate) && !now.isAfter(this.endDate))) status = IN_PROGRESS;
        else status = IN_PROGRESS;

        return Study.builder()
                .name(this.name)
                .description(this.description)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .status(status)
                .isDeleted(false)
                .teamId(teamId)
                .cropId(this.cropId)
                .build();
    }
}
