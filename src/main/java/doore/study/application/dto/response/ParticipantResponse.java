package doore.study.application.dto.response;

import doore.member.domain.Member;
import doore.member.domain.Participant;
import doore.member.domain.StudyRoleType;

public record ParticipantResponse(
        Long memberId,
        String name,
        String email,
        String imageUrl,
        StudyRoleType studyRole
) {

    public static ParticipantResponse of(final Participant participant, final StudyRoleType studyRole) {
        Member member = participant.getMember();
        return new ParticipantResponse(
                member.getId(),
                member.getName(),
                member.getEmail(),
                member.getImageUrl(),
                studyRole
        );
    }
}
