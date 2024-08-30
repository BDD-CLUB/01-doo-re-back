package doore.study.application.convenience;

import static doore.study.exception.StudyExceptionType.NOT_FOUND_STUDY;

import doore.study.domain.Study;
import doore.study.domain.repository.StudyRepository;
import doore.study.exception.StudyException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyAuthorization {
    private final StudyRepository studyRepository;

    public void validateExistStudy(Long studyId) {
        studyRepository.findById(studyId).orElseThrow(() -> new StudyException(NOT_FOUND_STUDY));
    }

    public Study getStudyOrThrow(final Long studyId) {
        return studyRepository.findById(studyId).orElseThrow(() -> new StudyException(NOT_FOUND_STUDY));
    }
}
