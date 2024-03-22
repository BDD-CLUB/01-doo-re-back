package doore.study.application.dto.response.totalStudyResponse;

import com.fasterxml.jackson.annotation.JsonFormat;
import doore.crop.domain.Crop;
import doore.crop.response.CropReferenceResponse;
import doore.study.domain.Study;
import doore.study.domain.StudyStatus;
import doore.study.persistence.dto.StudyInformation;
import doore.study.persistence.dto.StudyOverview;
import doore.team.application.dto.response.TeamReferenceResponse;
import doore.team.domain.Team;
import java.time.LocalDate;
import java.util.List;

public record StudySimpleResponse(

        Long id,
        String name,
        String description,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate endTime,
        StudyStatus status,
        Boolean isDeleted,
        TeamReferenceResponse teamReference,
        CropReferenceResponse cropReferenc,
        List<CurriculumItemReferenceResponse> curriculumItems
) {
    public static StudySimpleResponse of(final Study study, final Team team, final Crop crop) {
        return new StudySimpleResponse(study.getId(), study.getName(), study.getDescription(), study.getStartDate(),
                study.getEndDate(), study.getStatus(), study.getIsDeleted(),
                TeamReferenceResponse.from(team), CropReferenceResponse.from(crop),
                CurriculumItemReferenceResponse.from(study.getCurriculumItems()));
    }

    public static StudySimpleResponse of(final StudyInformation studyInformation,
                                         final List<StudyOverview> studyOverviews) {
        return new StudySimpleResponse(
                studyInformation.getId(),
                studyInformation.getName(),
                studyInformation.getDescription(),
                studyInformation.getStartDate().toLocalDate(),
                studyInformation.getEndDate().toLocalDate(),
                StudyStatus.valueOf(studyInformation.getStatus()),
                studyInformation.getIsDeleted(),
                new TeamReferenceResponse(
                        studyInformation.getTeamId(),
                        studyInformation.getTeamName(),
                        studyInformation.getTeamDescription(),
                        studyInformation.getTeamImageUrl()
                ),
                new CropReferenceResponse(
                        studyInformation.getCropId(),
                        studyInformation.getCropName(),
                        studyInformation.getCropImageUrl()
                ),
                CurriculumItemReferenceResponse.fromOverview(studyOverviews)
        );
    }
}
