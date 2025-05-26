package doore.study.application;

import static doore.member.exception.MemberExceptionType.UNAUTHORIZED;

import doore.member.application.convenience.StudyRoleConvenience;
import doore.member.application.convenience.StudyRoleValidateAccessPermission;
import doore.member.application.convenience.TeamRoleValidateAccessPermission;
import doore.member.domain.Participant;
import doore.member.domain.repository.ParticipantRepository;
import doore.member.exception.MemberException;
import doore.study.application.convenience.StudyConvenience;
import doore.study.application.convenience.StudyValidateAccessPermission;
import doore.study.application.dto.response.ParticipantResponse;
import doore.study.domain.Study;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ParticipantQueryService {

    private final ParticipantRepository participantRepository;

    private final StudyRoleConvenience studyRoleConvenience;
    private final StudyConvenience studyConvenience;

    private final StudyRoleValidateAccessPermission studyRoleValidateAccessPermission;
    private final TeamRoleValidateAccessPermission teamRoleValidateAccessPermission;
    private final StudyValidateAccessPermission studyValidateAccessPermission;

    public List<ParticipantResponse> getParticipants(final Long studyId, final Long memberId) {
        final Study study = studyConvenience.findById(studyId);
        studyValidateAccessPermission.validateExistStudy(studyId);
        checkStudyLeaderOrParticipantOrTeamLeader(studyId, memberId, study);
        List<Participant> participants = participantRepository.findAllByStudyId(studyId);
        return participants.stream()
                .map(participant -> ParticipantResponse.of(participant,
                        studyRoleConvenience.findStudyRoleType(studyId, participant.getMember().getId()))).toList();
    }

    private void checkStudyLeaderOrParticipantOrTeamLeader(final Long studyId, final Long memberId, final Study study) {
        final Long teamId = study.getTeamId();
        if (!(studyRoleValidateAccessPermission.isStudyLeader(studyId, memberId)
                || studyRoleValidateAccessPermission.isParticipant(studyId, memberId)
                || teamRoleValidateAccessPermission.isTeamLeader(teamId, memberId))) {
            throw new MemberException(UNAUTHORIZED);
        }
    }

}
