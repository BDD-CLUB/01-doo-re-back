package doore.study.application;

import static doore.crop.exception.CropExceptionType.NOT_FOUND_CROP;
import static doore.study.exception.StudyExceptionType.NOT_FOUND_STUDY;
import static doore.team.exception.TeamExceptionType.NOT_FOUND_TEAM;
import static java.util.stream.Collectors.groupingBy;

import doore.crop.domain.Crop;
import doore.crop.domain.repository.CropRepository;
import doore.crop.exception.CropException;
import doore.study.application.dto.response.CurriculumItemResponse;
import doore.study.application.dto.response.PersonalCurriculumItemResponse;
import doore.study.application.dto.response.StudyResponse;
import doore.study.application.dto.response.StudySimpleResponse;
import doore.study.domain.ParticipantCurriculumItem;
import doore.study.domain.Study;
import doore.study.domain.repository.CurriculumItemRepository;
import doore.study.domain.repository.ParticipantCurriculumItemRepository;
import doore.study.domain.repository.StudyRepository;
import doore.study.exception.StudyException;
import doore.study.persistence.StudyDao;
import doore.study.persistence.dto.StudyInformation;
import doore.study.persistence.dto.StudyOverview;
import doore.team.domain.Team;
import doore.team.domain.TeamRepository;
import doore.team.exception.TeamException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyQueryService {
    private final StudyRepository studyRepository;
    private final TeamRepository teamRepository;
    private final CropRepository cropRepository;
    private final StudyDao studyDao;
    private final CurriculumItemRepository curriculumItemRepository;
    private final ParticipantCurriculumItemRepository participantCurriculumItemRepository;

    public StudyResponse findStudyById(Long studyId) {
        Study study = studyRepository.findById(studyId).orElseThrow(() -> new StudyException(NOT_FOUND_STUDY));
        final Team team = teamRepository.findById(study.getTeamId())
                .orElseThrow(() -> new TeamException(NOT_FOUND_TEAM));
        final Crop crop = cropRepository.findById(study.getCropId())
                .orElseThrow(() -> new CropException(NOT_FOUND_CROP));
        return StudyResponse.of(study, team, crop);
    }

    public List<CurriculumItemResponse> getCurriculums(Long studyId) {
        return CurriculumItemResponse.from(curriculumItemRepository.findAllByStudyId(studyId));
    }

    public List<PersonalCurriculumItemResponse> getMyCurriculum(Long studyId, Long memberId) {
        List<ParticipantCurriculumItem> participantCurriculumItems = participantCurriculumItemRepository.findAllByStudyIdAndMemberId(studyId, memberId);
        return participantCurriculumItems.stream().map(PersonalCurriculumItemResponse::from).toList();
    }

    public List<StudySimpleResponse> findMyStudies(final Long memberId) {
        final List<StudyOverview> studyOverviews = studyDao.findMyStudy(memberId);
        final Map<StudyInformation, List<StudyOverview>> map = studyOverviews.stream()
                .collect(groupingBy(StudyOverview::getStudyInformation));
        return map.entrySet().stream()
                .peek(entry -> {
                    if (entry.getValue().stream().anyMatch(overview -> overview.getCurriculumName() == null)) {
                        entry.setValue(Collections.emptyList());
                    }
                    entry.setValue(entry.getValue().stream()
                            .sorted(Comparator.comparing(StudyOverview::getCurriculumItemOrder))
                            .toList());
                })
                .map(entry -> StudySimpleResponse.of(entry.getKey(), entry.getValue()))
                .toList();
    }
}
