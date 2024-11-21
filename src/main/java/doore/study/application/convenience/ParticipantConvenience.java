package doore.study.application.convenience;

import doore.member.domain.Member;
import doore.member.domain.Participant;
import doore.member.domain.repository.ParticipantRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ParticipantConvenience {
    private final ParticipantRepository participantRepository;

    public void assignParticipant(final Long studyId, final Member member) {
        participantRepository.save(Participant.builder()
                .studyId(studyId)
                .member(member)
                .build());
    }

    public void deleteParticipant(final Long studyId) {
        final List<Participant> participants = participantRepository.findAllByStudyId(studyId);
        participantRepository.deleteAll(participants);
    }
}
