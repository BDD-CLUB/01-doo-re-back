package doore.study;

import doore.member.domain.Member;
import doore.member.domain.Participant;
import org.springframework.stereotype.Component;

@Component
public class ParticipantFixture {

    public static Participant participant(Long studyId, Member member) {
        return Participant.builder()
                .studyId(studyId)
                .member(member)
                .build();
    }
}
