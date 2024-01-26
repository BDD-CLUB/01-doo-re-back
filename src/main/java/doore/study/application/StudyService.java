package doore.study.application;

import static doore.study.domain.StudyStatus.ENDED;
import static doore.study.exception.StudyExceptionType.*;

import doore.study.application.dto.request.StudyUpdateRequest;
import doore.study.application.dto.response.StudyDetailResponse;
import doore.study.application.dto.request.StudyCreateRequest;
import doore.study.domain.CurriculumItem;
import doore.study.domain.Study;
import doore.study.domain.repository.CurriculumItemRepository;
import doore.study.domain.repository.StudyRepository;
import doore.study.exception.StudyException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyService {
    private final StudyRepository studyRepository;
    private final CurriculumItemRepository curriculumItemRepository;

    public void createStudy(final StudyCreateRequest request, final Long teamId) {
        //todo: 존재하는 팀인지 유효성 검사
        Study study = studyRepository.save(request.toEntityWithoutCurriculum(teamId));
        List<CurriculumItem> curriculumItems = request.toCurriculumListEntity(study);
        curriculumItemRepository.saveAll(curriculumItems);
    }

    public void deleteStudy(Long studyId) {
        studyRepository.deleteById(studyId);
    }

    @Transactional(readOnly = true)
    public StudyDetailResponse findStudyById(Long studyId) {
        Study study = studyRepository.findById(studyId).orElseThrow(() -> new StudyException(NOT_FOUND_STUDY));
        return StudyDetailResponse.from(study);
    }

    public void updateStudy(StudyUpdateRequest request, Long studyId) {
        Study study = studyRepository.findById(studyId).orElseThrow(() -> new StudyException(NOT_FOUND_STUDY));
        study.update(request);
    }

    public StudyDetailResponse terminateStudy(Long studyId) {
        Study study = studyRepository.findById(studyId).orElseThrow(() -> new StudyException(NOT_FOUND_STUDY));
        if (study.getStatus() == ENDED) {
            throw new StudyException(TERMINATED_STUDY);
        }
        study.setStatus(ENDED);
        return StudyDetailResponse.from(study);
    }
}
