package doore.study.application.convenience;

import static doore.study.exception.StudyExceptionType.NOT_FOUND_STUDY;

import doore.study.domain.Study;
import doore.study.domain.repository.StudyRepository;
import doore.study.exception.StudyException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyConvenience {
    private final StudyRepository studyRepository;

    public Study findByDocumentId(final Long documentId) {
        return studyRepository.findByDocumentId(documentId);
    }

    public Study findByCurriculumItemId(final Long curriculumId) {
        return studyRepository.findByCurriculumItemId(curriculumId);
    }

    public List<Study> findAllByTeamIdAndMemberId(final Long teamId, final Long memberId) {
        return studyRepository.findAllByTeamIdAndMemberId(teamId, memberId);
    }

    public Study findById(final Long studyId) {
        return studyRepository.findById(studyId).orElseThrow(() -> new StudyException(NOT_FOUND_STUDY));
    }
}
