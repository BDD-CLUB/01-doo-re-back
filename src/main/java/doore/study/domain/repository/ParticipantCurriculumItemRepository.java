package doore.study.domain.repository;

import doore.study.domain.ParticipantCurriculumItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ParticipantCurriculumItemRepository extends JpaRepository<ParticipantCurriculumItem, Long> {
    Optional<ParticipantCurriculumItem> findByCurriculumItemIdAndParticipantId(Long curriculumId, Long participantId);

    @Query("select pc from ParticipantCurriculumItem pc "
            + "inner join Participant p on pc.participantId = p.id "
            + "where p.member.id = :memberId "
            + "and pc.curriculumItem.study.id = :studyId ")
    List<ParticipantCurriculumItem> findAllByStudyIdAndMemberId(Long studyId, Long memberId);

    List<ParticipantCurriculumItem> findAllByCurriculumItemId(Long curriculumId);

    void deleteAllByCurriculumItemId(Long curriculumItemId);

    long countByCurriculumItemIdIn(List<Long> curriculumIds);

    long countByCurriculumItemIdInAndIsCheckedTrue(List<Long> curriculumIds);
}
