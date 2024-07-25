package doore.study.application;

import static doore.member.domain.StudyRoleType.ROLE_스터디원;
import static doore.member.domain.StudyRoleType.ROLE_스터디장;
import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER_ROLE_IN_STUDY;
import static doore.member.exception.MemberExceptionType.UNAUTHORIZED;
import static doore.study.exception.StudyExceptionType.NOT_FOUND_STUDY;

import doore.member.domain.Participant;
import doore.member.domain.StudyRoleType;
import doore.member.domain.repository.ParticipantRepository;
import doore.member.domain.repository.StudyRoleRepository;
import doore.member.exception.MemberException;
import doore.study.application.dto.response.ParticipantResponse;
import doore.study.domain.repository.StudyRepository;
import doore.study.exception.StudyException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ParticipantQueryService {

    private final StudyRepository studyRepository;
    private final StudyRoleRepository studyRoleRepository;
    private final ParticipantRepository participantRepository;

    public List<ParticipantResponse> findAllParticipants(final Long studyId, final Long memberId) {
        validateExistStudyLeaderAndStudyMember(studyId, memberId);
        studyRepository.findById(studyId).orElseThrow(() -> new StudyException(NOT_FOUND_STUDY));
        List<Participant> participants = participantRepository.findAllByStudyId(studyId);
        return participants.stream()
                .map(participant -> ParticipantResponse.of(participant, getStudyRoleType(studyId, memberId))).toList();
    }

    private StudyRoleType getStudyRoleType(Long studyId, Long memberId) {
        return studyRoleRepository.findStudyRoleByStudyIdAndMemberId(studyId, memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ROLE_IN_STUDY))
                .getStudyRoleType();
    }

    private void validateExistStudyLeaderAndStudyMember(final Long studyId, final Long memberId) {
        final StudyRoleType studyRoleType = getStudyRoleType(studyId, memberId);
        if (!(studyRoleType.equals(ROLE_스터디장) || studyRoleType.equals(ROLE_스터디원))) {
            throw new MemberException(UNAUTHORIZED);
        }
    }
}
