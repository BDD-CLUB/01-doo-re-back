package doore.study.application.convenience;

import doore.study.domain.Study;
import doore.study.domain.repository.StudyRepository;
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
}
