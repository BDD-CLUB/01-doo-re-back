package doore.member.application.convenience;

import static doore.member.domain.StudyRoleType.ROLE_스터디원;
import static doore.member.domain.StudyRoleType.ROLE_스터디장;
import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER_ROLE_IN_STUDY;
import static doore.member.exception.MemberExceptionType.UNAUTHORIZED;

import doore.member.domain.StudyRole;
import doore.member.domain.repository.StudyRoleRepository;
import doore.member.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyRoleValidateAccessPermission {
    private final StudyRoleRepository studyRoleRepository;

    public void validateExistStudyLeader(final Long studyId, final Long memberId) {
        final StudyRole studyRole = studyRoleRepository.findStudyRoleByStudyIdAndMemberId(studyId, memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ROLE_IN_STUDY));
        if (!studyRole.getStudyRoleType().equals(ROLE_스터디장)) {
            throw new MemberException(UNAUTHORIZED);
        }
    }

    public StudyRole getValidateExistStudyLeader(final Long studyId, final Long memberId) {
        final StudyRole studyRole = studyRoleRepository.findStudyRoleByStudyIdAndMemberId(studyId, memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ROLE_IN_STUDY));
        if (!studyRole.getStudyRoleType().equals(ROLE_스터디장)) {
            throw new MemberException(UNAUTHORIZED);
        }
        return studyRole;
    }

    public void validateExistParticipantOnly(final Long studyId, final Long memberId) { // Only 스터디원인지 확인. Not 스터디장
        final StudyRole studyRole = studyRoleRepository.findStudyRoleByStudyIdAndMemberId(studyId, memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ROLE_IN_STUDY));
        if (!studyRole.getStudyRoleType().equals(ROLE_스터디원)) {
            throw new MemberException(UNAUTHORIZED);
        }
    }

    public void validateExistParticipant(final Long studyId, final Long memberId) { // Both 스터디원 and 스터디장
        studyRoleRepository.findStudyRoleByStudyIdAndMemberId(studyId, memberId)
                .orElseThrow(() -> new MemberException(UNAUTHORIZED));
    }

    public StudyRole getValidateExistParticipant(final Long studyId, final Long memberId) {
        return studyRoleRepository.findStudyRoleByStudyIdAndMemberId(studyId, memberId)
                .orElseThrow(() -> new MemberException(UNAUTHORIZED));
    }

    public boolean isStudyLeader(final Long studyId, final Long memberId) {
        return studyRoleRepository.findStudyRoleByStudyIdAndMemberId(studyId, memberId)
                .map(studyRole -> ROLE_스터디장.equals(studyRole.getStudyRoleType()))
                .orElse(false);
    }
}
