package doore.member.domain.repository;

import doore.member.domain.StudyRole;
import doore.member.domain.StudyRoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StudyRoleRepository extends JpaRepository<StudyRole, Long> {
    Boolean existsByStudyIdAndMemberId(Long studyId, Long memberId);

    Optional<StudyRole> findStudyRoleByStudyIdAndMemberId(Long studyId, Long memberId);

    Optional<StudyRole> findStudyRoleByStudyIdAndStudyRoleType(Long studyId, StudyRoleType studyRoleType);

    @Query("SELECT sr.memberId FROM StudyRole sr WHERE sr.studyId = :studyId AND sr.studyRoleType = 'ROLE_스터디장'")
    Long findLeaderIdByStudyId(Long studyId);
}
