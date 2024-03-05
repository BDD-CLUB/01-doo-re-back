package doore.study.application;

import static doore.study.exception.StudyExceptionType.NOT_FOUND_STUDY;

import doore.member.domain.Participant;
import doore.member.domain.repository.ParticipantRepository;
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
    private final ParticipantRepository participantRepository;

    public List<Participant> findAllParticipants(Long studyId) {
        studyRepository.findById(studyId).orElseThrow(() -> new StudyException(NOT_FOUND_STUDY));
        return participantRepository.findAllByStudyId(studyId);
    }
}
