package doore.study.application;

import static doore.member.exception.ParticipantExceptionType.CANNOT_DELETE_STUDY_LEADER;

import doore.member.application.convenience.MemberValidateAccessPermission;
import doore.member.application.convenience.StudyRoleConvenience;
import doore.member.application.convenience.StudyRoleValidateAccessPermission;
import doore.member.domain.Member;
import doore.member.domain.repository.ParticipantRepository;
import doore.member.exception.ParticipantException;
import doore.study.application.convenience.ParticipantConvenience;
import doore.study.application.convenience.StudyValidateAccessPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ParticipantCommandService {
    private final ParticipantRepository participantRepository;

    private final StudyRoleConvenience studyRoleConvenience;
    private final ParticipantConvenience participantConvenience;

    private final StudyRoleValidateAccessPermission studyRoleValidateAccessPermission;
    private final MemberValidateAccessPermission memberValidateAccessPermission;
    private final StudyValidateAccessPermission studyValidateAccessPermission;

    public void createParticipant(final Long studyId, final Long memberId, final Long studyLeaderId) {
        studyRoleValidateAccessPermission.validateExistStudyLeader(studyId, studyLeaderId);
        studyValidateAccessPermission.validateExistStudy(studyId);
        final Member member = memberValidateAccessPermission.getValidateExistMember(memberId);
        participantConvenience.assignParticipant(studyId, member);
        studyRoleConvenience.assignParticipantRole(studyId, memberId, studyLeaderId);
    }

    public void deleteParticipant(final Long studyId, final Long memberId, final Long studyLeaderId) {
        studyRoleValidateAccessPermission.validateExistStudyLeader(studyId, studyLeaderId);
        studyValidateAccessPermission.validateExistStudy(studyId);
        checkIsEqualDeleteMemberIdAndStudyLeaderId(memberId, studyLeaderId);
        final Member member = memberValidateAccessPermission.getValidateExistMember(memberId);
        participantRepository.deleteByStudyIdAndMember(studyId, member);
    }

    public void withdrawParticipant(final Long studyId, final Long memberId, final Long participantId) {
        studyRoleValidateAccessPermission.validateExistParticipant(studyId, participantId);
        studyValidateAccessPermission.validateExistStudy(studyId);
        final Member member = memberValidateAccessPermission.getValidateExistMember(memberId);
        participantRepository.deleteByStudyIdAndMember(studyId, member);
    }

    private void checkIsEqualDeleteMemberIdAndStudyLeaderId(final Long deleteMemberId, final Long studyLeaderId) {
        if (deleteMemberId.equals(studyLeaderId)) {
            throw new ParticipantException(CANNOT_DELETE_STUDY_LEADER);
        }
    }
}
