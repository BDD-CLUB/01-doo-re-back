package doore.study.application;

import doore.member.application.convenience.StudyRoleConvenience;
import doore.member.application.convenience.StudyRoleValidateAccessPermission;
import doore.member.domain.Participant;
import doore.member.domain.repository.ParticipantRepository;
import doore.study.application.convenience.StudyValidateAccessPermission;
import doore.study.application.dto.response.ParticipantResponse;
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

    private final StudyRoleValidateAccessPermission studyRoleValidateAccessPermission;
    private final StudyValidateAccessPermission studyValidateAccessPermission;

    public List<ParticipantResponse> getParticipants(final Long studyId, final Long memberId) {
        studyRoleValidateAccessPermission.validateExistParticipant(studyId, memberId);
        studyValidateAccessPermission.validateExistStudy(studyId);
        List<Participant> participants = participantRepository.findAllByStudyIdAndIsDeletedFalse(studyId);
        return participants.stream()
                .map(participant -> ParticipantResponse.of(participant,
                        studyRoleConvenience.findStudyRoleType(studyId, memberId))).toList();
    }
}
