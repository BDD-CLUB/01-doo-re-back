package doore.study.application;

import static doore.study.exception.StudyExceptionType.NOT_FOUND_STUDY;

import doore.study.application.dto.response.StudyDetailResponse;
import doore.study.domain.Study;
import doore.study.domain.repository.StudyRepository;
import doore.study.exception.StudyException;
import doore.member.domain.Participant;
import doore.member.domain.repository.ParticipantRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyQueryService {
    private final StudyRepository studyRepository;
    private final ParticipantRepository participantRepository;

    public StudyDetailResponse findStudyById(Long studyId) {
        Study study = studyRepository.findById(studyId).orElseThrow(() -> new StudyException(NOT_FOUND_STUDY));
        return StudyDetailResponse.from(study);
    }

    public List<Participant> findAllParticipants(Long studyId) {
        studyRepository.findById(studyId).orElseThrow(() -> new StudyException(NOT_FOUND_STUDY));
        return participantRepository.findAllByStudyId(studyId);
    }
}
