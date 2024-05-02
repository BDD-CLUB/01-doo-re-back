package doore.garden.domain;

import static doore.garden.domain.GardenType.COMPLETE_STUDY_CURRICULUM;

import doore.document.domain.Document;
import doore.study.domain.ParticipantCurriculumItem;
import java.time.LocalDate;

public class ContributionGardens implements GardenInterface {

    public Garden create(Document document) {
        return Garden.builder()
                .contributedDate(LocalDate.now())
                .contributionId(document.getId())
                .memberId(document.getUploaderId())
                .teamId(document.getGroupId())
                .type(GardenType.DOCUMENT_UPLOAD)
                .build();
    }

    public Garden create(ParticipantCurriculumItem participantCurriculumItem) {
        return Garden.builder()
                .contributedDate(LocalDate.now())
                .contributionId(participantCurriculumItem.getId())
                .memberId(participantCurriculumItem.getParticipantId())
                .teamId(participantCurriculumItem.getCurriculumItem().getStudy().getTeamId())
                .type(COMPLETE_STUDY_CURRICULUM)
                .build();
    }

}


