package doore.study.domain.repository;

import doore.study.domain.Study;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StudyRepository extends JpaRepository<Study, Long> {

    @Query("select s from Study s join Participant p on p.studyId=s.id where p.member.id=:memberId")
    List<Study> findAllByMemberId(final Long memberId);

    @Query("select s from Study s join s.curriculumItems ci on ci.study.id = s.id where ci.id = :curriculumItemId ")
    Study findByCurriculumItemId(final Long curriculumItemId);

    List<Study> findAllByTeamId(final Long teamId);

    @Query("select s from Study s join Document d on d.groupId = s.id where d.id = :documentId")
    Study findByDocumentId(Long documentId);
}
