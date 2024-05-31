package doore.study.application.dto.response;

import doore.study.domain.ParticipantCurriculumItem;
import java.util.List;

public record ParticipantCurriculumItemResponse(
        Long id,
        Long participantId,
        Boolean isChecked
) {
    public static List<ParticipantCurriculumItemResponse> from(
            final List<ParticipantCurriculumItem> participantCurriculumItems) {
        return participantCurriculumItems.stream()
                .map(participantCurriculumItem -> new ParticipantCurriculumItemResponse(
                        participantCurriculumItem.getId(),
                        participantCurriculumItem.getParticipantId(),
                        participantCurriculumItem.getIsChecked()
                ))
                .toList();
    }
}
