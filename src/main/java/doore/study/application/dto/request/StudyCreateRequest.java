package doore.study.application.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import doore.study.domain.CurriculumItem;
import doore.study.domain.Study;
import doore.study.domain.StudyStatus;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import lombok.Builder;

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

        @NotNull(message = "현재 상태를 입력해주세요.")
        StudyStatus status,

        @NotNull
        Boolean isDeleted,

        @NotNull(message = "작물을 골라주세요.")
        Long cropId,

        @Nullable
        List<CurriculumItemsRequest> curriculumItems
) {
    @Builder
    public StudyCreateRequest(String name, String description, LocalDate startDate, LocalDate endDate,
                              StudyStatus status, Boolean isDeleted, Long cropId,
                              List<CurriculumItemsRequest> curriculumItems) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.isDeleted = isDeleted;
        this.cropId = cropId;
        this.curriculumItems = curriculumItems;
    }

    public Study toEntityWithoutCurriculum(Long teamId) {
        return Study.builder()
                .name(this.name())
                .description(this.description())
                .startDate(this.startDate())
                .endDate(this.endDate())
                .status(this.status())
                .isDeleted(this.isDeleted())
                .teamId(teamId)
                .cropId(this.cropId())
                .build();
    }

    public List<CurriculumItem> toCurriculumListEntity(Study study) {
        if (this.curriculumItems == null) {
            return Collections.emptyList();
        }
        return curriculumItems.stream()
                .map(curriculumItemsRequest -> curriculumItemsRequest.toEntity(study))
                .toList();
    }
}
