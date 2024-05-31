package doore.study.application.dto.response;

import doore.study.domain.ParticipantCurriculumItem;
import lombok.Builder;

public record PersonalCurriculumItemResponse(
        Long id,
        Long participantId,
        String name,
        Integer itemOrder,
        Boolean isChecked
) {
    @Builder
    public PersonalCurriculumItemResponse(Long id, Long participantId, String name, Integer itemOrder,
                                          Boolean isChecked) {
        this.id = id;
        this.participantId = participantId;
        this.name = name;
        this.itemOrder = itemOrder;
        this.isChecked = isChecked;
    }

    public static PersonalCurriculumItemResponse from(ParticipantCurriculumItem participantCurriculumItem) {
        return PersonalCurriculumItemResponse.builder()
                .id(participantCurriculumItem.getId())
                .participantId(participantCurriculumItem.getParticipantId())
                .name(participantCurriculumItem.getCurriculumItem().getName())
                .itemOrder(participantCurriculumItem.getCurriculumItem().getItemOrder())
                .isChecked(participantCurriculumItem.getIsChecked())
                .build();
    }
}
