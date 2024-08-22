package doore.study.application.convenience;

import doore.study.domain.Study;
import doore.study.domain.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyConvenience {
    private final StudyRepository studyRepository;

    public Study findByCurriculumItemId(Long curriculumId) {
        return studyRepository.findByCurriculumItemId(curriculumId);
    }
}
