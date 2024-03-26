package doore.member.domain.repository;

import doore.member.domain.Member;
import doore.member.domain.Participant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    void deleteByStudyIdAndMember(Long studyId, Member member);

    List<Participant> findAllByStudyId(Long studyId);

    boolean existsByStudyIdAndMemberId(Long studyId, Long memberId);
}
