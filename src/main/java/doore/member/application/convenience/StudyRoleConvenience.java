package doore.member.application.convenience;

import static doore.member.domain.StudyRoleType.ROLE_스터디원;
import static doore.member.domain.StudyRoleType.ROLE_스터디장;
import static doore.member.exception.MemberExceptionType.CANNOT_DELETE_STUDY_LEADER;
import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER_ROLE_IN_STUDY;

import doore.member.domain.StudyRole;
import doore.member.domain.StudyRoleType;
import doore.member.domain.repository.StudyRoleRepository;
import doore.member.exception.MemberException;
import doore.study.domain.Study;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyRoleConvenience {
    private final StudyRoleRepository studyRoleRepository;

    public void assignStudyLeaderRole(final Long studyId, final Long memberId) {
        studyRoleRepository.save(StudyRole.builder()
                .studyRoleType(ROLE_스터디장)
                .studyId(studyId)
                .memberId(memberId)
                .build());
    }

    public void assignParticipantRole(Long studyId, Long memberId, Long studyLeaderId) {
        if (!memberId.equals(studyLeaderId)) {
            studyRoleRepository.save(StudyRole.builder()
                    .studyRoleType(ROLE_스터디원)
                    .studyId(studyId)
                    .memberId(memberId)
                    .build());
        }
    }

    public StudyRoleType findStudyRoleType(final Long studyId, final Long memberId) {
        return studyRoleRepository.findStudyRoleByStudyIdAndMemberId(studyId, memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ROLE_IN_STUDY))
                .getStudyRoleType();
    }

    public Long findStudyLeaderId(final Long studyId) {
        return studyRoleRepository.findLeaderIdByStudyId(studyId);
    }

    public void isStudyLeader(final List<Study> studies, final Long memberId) {
        final List<String> leaderStudyNames = studies.stream()
                .filter(study -> studyRoleRepository.existsByStudyIdAndMemberIdAndStudyRoleType(
                        study.getId(), memberId, ROLE_스터디장))
                .map(Study::getName)
                .toList();

        if (!leaderStudyNames.isEmpty()) {
            String joinedNames = String.join(", ", leaderStudyNames);
            String formattedMessage = String.format(CANNOT_DELETE_STUDY_LEADER.errorMessage(), joinedNames);
            throw new MemberException(CANNOT_DELETE_STUDY_LEADER, formattedMessage);
        }
    }

    public void deleteByStudyIdAndMemberId(final Long studyId, final Long memberId) {
        studyRoleRepository.deleteByStudyIdAndMemberId(studyId, memberId);
    }

    public Optional<StudyRole> findStudyRoleByStudyIdAndMemberId(final Long studyId, final Long memberId) {
        return studyRoleRepository.findStudyRoleByStudyIdAndMemberId(studyId, memberId);
    }
}
