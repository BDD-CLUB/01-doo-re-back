package doore.study.application;

import static doore.crop.exception.CropExceptionType.NOT_FOUND_CROP;
import static doore.study.exception.StudyExceptionType.NOT_FOUND_STUDY;
import static doore.team.exception.TeamExceptionType.NOT_FOUND_TEAM;

import doore.crop.domain.Crop;
import doore.crop.domain.repository.CropRepository;
import doore.crop.exception.CropException;
import doore.study.application.dto.response.personalStudyResponse.PersonalCurriculumItemResponse;
import doore.study.application.dto.response.personalStudyResponse.PersonalStudyDetailResponse;
import doore.study.application.dto.response.totalStudyResponse.CurriculumItemResponse;
import doore.study.application.dto.response.totalStudyResponse.ParticipantCurriculumItemResponse;
import doore.study.application.dto.response.totalStudyResponse.StudyDetailResponse;
import doore.study.application.dto.response.totalStudyResponse.StudySimpleResponse;
import doore.study.domain.CurriculumItem;
import doore.study.domain.Study;
import doore.study.domain.repository.StudyRepository;
import doore.study.exception.StudyException;
import doore.team.domain.Team;
import doore.team.domain.TeamRepository;
import doore.team.exception.TeamException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
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

    public StudyDetailResponse findStudyById(Long studyId) {
        Study study = getStudy(studyId);
        final Team team = teamRepository.findById(study.getTeamId())
                .orElseThrow(() -> new TeamException(NOT_FOUND_TEAM));
        final Crop crop = cropRepository.findById(study.getCropId())
                .orElseThrow(() -> new CropException(NOT_FOUND_CROP));
        return StudyDetailResponse.of(study, team, crop);
    }


    public PersonalStudyDetailResponse getPersonalStudyDetail(Long studyId, Long memberId) {
        Study study = getStudy(studyId);
        final Team team = teamRepository.findById(study.getTeamId())
                .orElseThrow(() -> new TeamException(NOT_FOUND_TEAM));
        final Crop crop = cropRepository.findById(study.getCropId())
                .orElseThrow(() -> new CropException(NOT_FOUND_CROP));
        List<CurriculumItemResponse> curriculumItemResponses = getListCurriculumItemResponse(study);
        List<PersonalCurriculumItemResponse> personalCurriculumItemResponse = curriculumItemResponses.stream()
                .filter(curriculumItemResponse -> curriculumItemResponse.participantCurriculumItems().stream()
                        .filter(participantCurriculumItemResponse ->
                                participantCurriculumItemResponse.participantId().equals(memberId))
                        .isParallel()
                )
                .map(this::toPersonalCurriculumItemResponse)
                .toList();

        return PersonalStudyDetailResponse.of(study, team, crop, memberId, personalCurriculumItemResponse);
    }

    private List<CurriculumItemResponse> getListCurriculumItemResponse(Study study) {
        List<CurriculumItem> curriculumItems = study.getCurriculumItems();

        return curriculumItems == null ? Collections.emptyList() : curriculumItems.stream()
                .map(this::toCurriculumItemResponse)
                .toList();
    }

    private CurriculumItemResponse toCurriculumItemResponse(CurriculumItem curriculumItem) {
        List<ParticipantCurriculumItemResponse> participantCurriculumItemResponses = curriculumItem.getParticipantCurriculumItems()
                .stream()
                .map((participantCurriculumItem) -> new ParticipantCurriculumItemResponse(
                        participantCurriculumItem.getParticipantId(), participantCurriculumItem.getIsChecked()))
                .toList();

        return new CurriculumItemResponse(curriculumItem.getId(), curriculumItem.getName(),
                curriculumItem.getItemOrder(), curriculumItem.getIsDeleted(), participantCurriculumItemResponses);
    }

    private PersonalCurriculumItemResponse toPersonalCurriculumItemResponse(
            CurriculumItemResponse curriculumItemResponse) {
        return new PersonalCurriculumItemResponse(curriculumItemResponse.id(), curriculumItemResponse.name(),
                curriculumItemResponse.itemOrder(), curriculumItemResponse.isDeleted(),
                curriculumItemResponse.participantCurriculumItems().get(0).isChecked());
    }


    private Study getStudy(Long studyId) {
        return studyRepository.findById(studyId).orElseThrow(() -> new StudyException(NOT_FOUND_STUDY));
    }

    public List<StudySimpleResponse> findMyStudies(final Long memberId) {
        return studyRepository.findAllByMemberId(memberId)
                .stream()
                .map(study -> StudySimpleResponse.of(study, Team.builder().build(), Crop.builder().build()))
                .collect(Collectors.toList());
        // TODO: 3/21/24 team과 crop을 어떻게 가지고 올지 고민하기
    }
}
