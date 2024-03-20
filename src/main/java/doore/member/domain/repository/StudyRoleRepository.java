package doore.member.domain.repository;

import doore.member.domain.StudyRole;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRoleRepository extends JpaRepository<StudyRole, Long> {
    Optional<StudyRole> findStudyRoleByStudyIdAndMemberId(Long studyId, Long memberId);
}
