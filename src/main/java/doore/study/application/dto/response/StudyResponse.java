package doore.study.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import doore.crop.domain.Crop;
import doore.crop.response.CropReferenceResponse;
import doore.study.domain.Study;
import doore.study.domain.StudyStatus;
import doore.team.application.dto.response.TeamReferenceResponse;
import doore.team.domain.Team;
import java.time.LocalDate;
import lombok.Builder;

public record StudyResponse(
        Long id,
        String name,
        String description,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate endDate,
        StudyStatus status,
        TeamReferenceResponse teamReference,
        CropReferenceResponse cropReference
) {
    @Builder
    public StudyResponse(Long id, String name, String description, LocalDate startDate, LocalDate endDate,
                         StudyStatus status, TeamReferenceResponse teamReference, CropReferenceResponse cropReference) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.teamReference = teamReference;
        this.cropReference = cropReference;
    }

    public static StudyResponse of(final Study study, final Team team, final Crop crop) {
        return StudyResponse.builder()
                .id(study.getId())
                .name(study.getName())
                .description(study.getDescription())
                .startDate(study.getStartDate())
                .endDate(study.getEndDate())
                .status(study.getStatus())
                .teamReference(TeamReferenceResponse.from(team))
                .cropReference(CropReferenceResponse.from(crop))
                .build();
    }
}
