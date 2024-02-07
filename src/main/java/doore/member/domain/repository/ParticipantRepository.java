package doore.member.domain.repository;

import doore.member.domain.Member;
import doore.member.domain.Participant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    void deleteByStudyIdAndMember(Long studyId, Member member);

    List<Participant> findAllByStudyId(Long studyId);
}
