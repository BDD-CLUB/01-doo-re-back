package doore.study.application;

import static doore.member.domain.StudyRoleType.ROLE_스터디원;
import static doore.member.domain.StudyRoleType.ROLE_스터디장;
import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER_ROLE_IN_STUDY;
import static doore.member.exception.MemberExceptionType.UNAUTHORIZED;
import static doore.study.exception.StudyExceptionType.NOT_FOUND_STUDY;

import doore.member.domain.Participant;
import doore.member.domain.StudyRole;
import doore.member.domain.repository.ParticipantRepository;
import doore.member.domain.repository.StudyRoleRepository;
import doore.member.exception.MemberException;
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

    public List<Participant> findAllParticipants(Long studyId, Long memberId) {
        validateExistStudyLeaderAndStudyMember(memberId);
        studyRepository.findById(studyId).orElseThrow(() -> new StudyException(NOT_FOUND_STUDY));
        return participantRepository.findAllByStudyId(studyId);
    }

    private void validateExistStudyLeaderAndStudyMember(Long memberId) {
        StudyRole studyRole = studyRoleRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ROLE_IN_STUDY));
        if (!(studyRole.getStudyRoleType().equals(ROLE_스터디장) || studyRole.getStudyRoleType().equals(ROLE_스터디원))){
            throw new MemberException(UNAUTHORIZED);
        }
    }
}
