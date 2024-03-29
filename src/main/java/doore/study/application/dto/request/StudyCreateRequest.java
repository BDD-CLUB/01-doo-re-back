package doore.study.application.dto.request;

import static doore.study.domain.StudyStatus.UPCOMING;

import com.fasterxml.jackson.annotation.JsonFormat;
import doore.study.domain.CurriculumItem;
import doore.study.domain.Study;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
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
        Long cropId,

        @NotNull(message = "커리큘럼을 입력해주세요.")
        List<CurriculumItemRequest> curriculumItems
) {
    public StudyCreateRequest(String name, String description, LocalDate startDate, LocalDate endDate,
                              Long cropId, List<CurriculumItemRequest> curriculumItems) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.cropId = cropId;
        this.curriculumItems =  Collections.emptyList();
    }
}
