package doore.member.application;

import static doore.member.exception.MemberTeamExceptionType.CANNOT_DELETE_TEAM_LEADER;

import doore.member.application.convenience.StudyRoleConvenience;
import doore.member.application.convenience.TeamRoleConvenience;
import doore.member.application.convenience.TeamRoleValidateAccessPermission;
import doore.member.domain.repository.MemberTeamRepository;
import doore.member.exception.MemberTeamException;
import doore.study.application.convenience.ParticipantConvenience;
import doore.study.application.convenience.StudyConvenience;
import doore.study.domain.Study;
import doore.team.application.convenience.TeamValidateAccessPermission;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberTeamCommandService {
    private final MemberTeamRepository memberTeamRepository;

    private final TeamRoleValidateAccessPermission teamRoleValidateAccessPermission;
    private final TeamValidateAccessPermission teamValidateAccessPermission;

    private final TeamRoleConvenience teamRoleConvenience;
    private final StudyConvenience studyConvenience;
    private final StudyRoleConvenience studyRoleConvenience;
    private final ParticipantConvenience participantConvenience;

    public void deleteMemberTeam(final Long teamId, final Long deleteMemberId, final Long teamLeaderId) {
        teamValidateAccessPermission.validateExistTeam(teamId);
        checkIsEqualDeleteMemberIdAndTeamLeaderId(deleteMemberId, teamLeaderId);
        teamRoleValidateAccessPermission.validateExistTeamLeader(teamId, teamLeaderId);
        teamRoleValidateAccessPermission.validateExistMemberTeam(teamId, deleteMemberId);
        deleteStudiesAndParticipants(teamId, deleteMemberId);
        memberTeamRepository.deleteByTeamIdAndMemberId(teamId, deleteMemberId);
        teamRoleConvenience.deleteByTeamIdAndMemberId(teamId, deleteMemberId);
    }

    private void checkIsEqualDeleteMemberIdAndTeamLeaderId(final Long deleteMemberId, final Long teamLeaderId) {
        if (deleteMemberId.equals(teamLeaderId)) {
            throw new MemberTeamException(CANNOT_DELETE_TEAM_LEADER);
        }
    }

    private void deleteStudiesAndParticipants(final Long teamId, final Long deleteMemberId) {
        List<Study> studies = studyConvenience.findAllByTeamIdAndMemberId(teamId, deleteMemberId);
        studyRoleConvenience.isStudyLeader(studies, deleteMemberId);

        studies.forEach(study -> {
            participantConvenience.deleteByParticipantIdAndMemberId(study.getId(), deleteMemberId);
            studyRoleConvenience.deleteByStudyIdAndMemberId(study.getId(), deleteMemberId);
        });
    }
}
