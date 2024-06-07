package doore.study;

import doore.study.domain.CurriculumItem;
import doore.study.domain.ParticipantCurriculumItem;
import org.springframework.stereotype.Component;

@Component
public class ParticipantCurriculumItemFixture {

    public static ParticipantCurriculumItem participantCurriculumItem(Long participantId, CurriculumItem curriculumItem) {
        return ParticipantCurriculumItem.builder()
                .participantId(participantId)
                .curriculumItem(curriculumItem)
                .build();
    }
}
