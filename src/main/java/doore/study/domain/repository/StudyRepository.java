package doore.study.domain.repository;

import doore.study.domain.Study;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StudyRepository extends JpaRepository<Study, Long> {

    @Query("select s from Study s join Participant p on p.studyId=s.id where p.member.id=:memberId")
    List<Study> findAllByMemberId(final Long memberId);
}
