package doore.study.application;

import doore.member.application.convenience.MemberValidateAccessPermission;
import doore.member.application.convenience.StudyRoleConvenience;
import doore.member.application.convenience.StudyRoleValidateAccessPermission;
import doore.member.domain.Member;
import doore.member.domain.repository.ParticipantRepository;
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
        studyRoleConvenience.duplicateCheckStudyParticipant(studyId, memberId);
        final Member member = memberValidateAccessPermission.getValidateExistMember(memberId);
        participantConvenience.assignParticipant(studyId, member);
        studyRoleConvenience.assignParticipantRole(studyId, memberId, studyLeaderId);
    }

    public void deleteParticipant(final Long studyId, final Long memberId, final Long studyLeaderId) {
        studyRoleValidateAccessPermission.validateExistStudyLeader(studyId, studyLeaderId);
        studyValidateAccessPermission.validateExistStudy(studyId);
        final Member member = memberValidateAccessPermission.getValidateExistMember(memberId);
        participantRepository.deleteByStudyIdAndMember(studyId, member);
    }

    public void withdrawParticipant(final Long studyId, final Long memberId, final Long participantId) {
        studyRoleValidateAccessPermission.validateExistParticipant(studyId, participantId);
        studyValidateAccessPermission.validateExistStudy(studyId);
        final Member member = memberValidateAccessPermission.getValidateExistMember(memberId);
        participantRepository.deleteByStudyIdAndMember(studyId, member);
    }
}
