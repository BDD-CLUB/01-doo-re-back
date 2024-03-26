package doore.study.application;

import static doore.study.exception.StudyExceptionType.NOT_A_PARTICIPANT;
import static doore.study.exception.StudyExceptionType.NOT_ENDED_STUDY;
import static doore.study.exception.StudyExceptionType.NOT_FOUND_STUDY;

import doore.member.domain.repository.ParticipantRepository;
import doore.study.application.dto.response.personalStudyResponse.PersonalStudyDetailResponse;
import doore.study.domain.Study;
import doore.study.domain.StudyStatus;
import doore.study.domain.repository.StudyRepository;
import doore.study.exception.StudyException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyCardQueryService {
    private final StudyRepository studyRepository;
    private final StudyQueryService studyQueryService;
    private final ParticipantRepository participantRepository;

    public List<PersonalStudyDetailResponse> getStudyCards(Long memberId) {
        List<Study> joinedStudies = studyRepository.findAllByMemberIdAndStatus(memberId, StudyStatus.ENDED);
        List<PersonalStudyDetailResponse> endedStudyResponses = new ArrayList<>();
        for (Study joinedStudy : joinedStudies) {
            endedStudyResponses.add(studyQueryService.getPersonalStudyDetail(joinedStudy.getId(), memberId));
        }
        return endedStudyResponses;
    }

    public PersonalStudyDetailResponse getStudyCard(Long studyId, Long memberId) {
        Study study = studyRepository.findById(studyId).orElseThrow(() -> new StudyException(NOT_FOUND_STUDY));
        if (memberNotJoined(study.getId(), memberId)) {
            throw new StudyException(NOT_A_PARTICIPANT);
        }
        if (isNotEndedStudy(studyId)) {
            throw new StudyException(NOT_ENDED_STUDY);
        }
        return studyQueryService.getPersonalStudyDetail(study.getId(), memberId);
    }

    private boolean isNotEndedStudy(Long studyId) {
        Study study = studyRepository.findById(studyId).orElseThrow(() -> new StudyException(NOT_FOUND_STUDY));
        return !(study.getStatus() == StudyStatus.ENDED);
    }

    private boolean memberNotJoined(Long studyId, Long memberId) {
        return !participantRepository.existsByStudyIdAndMemberId(studyId, memberId);
    }
}
