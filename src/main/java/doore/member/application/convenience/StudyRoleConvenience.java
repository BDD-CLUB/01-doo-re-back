package doore.member.application.convenience;

import doore.member.domain.StudyRole;
import doore.member.domain.StudyRoleType;
import doore.member.domain.repository.StudyRoleRepository;
import doore.member.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static doore.member.domain.StudyRoleType.ROLE_스터디원;
import static doore.member.domain.StudyRoleType.ROLE_스터디장;
import static doore.member.exception.MemberExceptionType.ALREADY_JOIN_STUDY_MEMBER;
import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER_ROLE_IN_STUDY;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyRoleConvenience {
    private final StudyRoleRepository studyRoleRepository;

    public void duplicateCheckStudyParticipant(final Long studyId, final Long memberId) {
        if (studyRoleRepository.existsByStudyIdAndMemberId(studyId, memberId)) {
            throw new MemberException(ALREADY_JOIN_STUDY_MEMBER);
        }
    }

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

    public StudyRoleType findStudyRoleType(Long studyId, Long memberId) {
        return studyRoleRepository.findStudyRoleByStudyIdAndMemberId(studyId, memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ROLE_IN_STUDY))
                .getStudyRoleType();
    }

    public Long findStudyLeaderId(final Long studyId) {
        return studyRoleRepository.findLeaderIdByStudyId(studyId);
    }
}
