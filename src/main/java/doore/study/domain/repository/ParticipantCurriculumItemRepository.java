package doore.study.domain.repository;

import doore.study.domain.ParticipantCurriculumItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ParticipantCurriculumItemRepository extends JpaRepository<ParticipantCurriculumItem, Long> {
    Optional<ParticipantCurriculumItem> findByCurriculumItemIdAndParticipantId(Long curriculumId, Long participantId);

    @Query("select pc from ParticipantCurriculumItem pc "
            + "inner join CurriculumItem c on c = pc.curriculumItem "
            + "inner join Participant p on pc.participantId = p.id "
            + "inner join Member m on p.member.id = m.id "
            + "where m.id = :memberId "
            + "and c.study.id = :studyId ")
    List<ParticipantCurriculumItem> findAllByStudyIdAndMemberId(Long studyId, Long memberId);
}
