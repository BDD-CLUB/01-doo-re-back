package doore.study.application.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import doore.study.domain.StudyStatus;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record StudyUpdateRequest(
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

        @NotNull(message = "현재 상태를 입력해주세요.")
        StudyStatus status
) {
    public StudyUpdateRequest(String name, String description, LocalDate startDate, LocalDate endDate,
                              StudyStatus status) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }
}
