package doore.study.application;

import static doore.crop.exception.CropExceptionType.NOT_FOUND_CROP;
import static doore.study.exception.StudyExceptionType.NOT_FOUND_STUDY;
import static doore.team.exception.TeamExceptionType.NOT_FOUND_TEAM;

import doore.crop.domain.Crop;
import doore.crop.domain.repository.CropRepository;
import doore.crop.exception.CropException;
import doore.crop.response.CropReferenceResponse;
import doore.study.application.dto.response.StudyResponse;
import doore.study.application.dto.response.personalStudyResponse.PersonalCurriculumItemResponse;
import doore.study.application.dto.response.personalStudyResponse.PersonalStudyDetailResponse;
import doore.study.application.dto.response.totalStudyResponse.CurriculumItemResponse;
import doore.study.application.dto.response.totalStudyResponse.ParticipantCurriculumItemResponse;
import doore.study.application.dto.response.totalStudyResponse.StudyDetailResponse;
import doore.study.domain.CurriculumItem;
import doore.study.domain.Study;
import doore.study.domain.repository.StudyRepository;
import doore.study.exception.StudyException;
import doore.team.application.dto.response.TeamReferenceResponse;
import doore.team.domain.Team;
import doore.team.domain.repository.TeamRepository;
import doore.team.exception.TeamException;
import java.util.Collections;
import java.util.List;
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
        List<CurriculumItemResponse> curriculumItemResponses = getListCurriculumItemResponse(study);
        StudyResponse studyResponse = toStudyResponse(study);

        return new StudyDetailResponse(studyResponse, curriculumItemResponses);
    }


    public PersonalStudyDetailResponse getPersonalStudyDetail(Long studyId, Long memberId) {
        Study study = getStudy(studyId);
        StudyResponse studyResponse = toStudyResponse(study);
        List<CurriculumItemResponse> curriculumItemResponses = getListCurriculumItemResponse(study);
        List<PersonalCurriculumItemResponse> personalCurriculumItemResponse = curriculumItemResponses.stream()
                .filter(curriculumItemResponse -> curriculumItemResponse.participantCurriculumItems().stream()
                        .filter(participantCurriculumItemResponse ->
                                participantCurriculumItemResponse.participantId().equals(memberId))
                        .isParallel()
                )
                .map(this::toPersonalCurriculumItemResponse)
                .toList();

        return new PersonalStudyDetailResponse(studyResponse, memberId, personalCurriculumItemResponse);
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

    private StudyResponse toStudyResponse(Study study) {
        Team team = teamRepository.findById(study.getTeamId())
                .orElseThrow(() -> new TeamException(NOT_FOUND_TEAM));
        TeamReferenceResponse teamReferenceResponse =
                new TeamReferenceResponse(team.getId(), team.getName(), team.getDescription(), team.getImageUrl());
        Crop crop = cropRepository.findById(study.getCropId()).orElseThrow(() -> new CropException(NOT_FOUND_CROP));
        CropReferenceResponse cropReferenceResponse =
                new CropReferenceResponse(crop.getId(), crop.getName(), crop.getImageUrl());

        return new StudyResponse(study.getId(), study.getName(), study.getDescription(), study.getStartDate(),
                study.getEndDate(), study.getStatus(), study.getIsDeleted(), teamReferenceResponse,
                cropReferenceResponse);
    }

    private Study getStudy(Long studyId) {
        return studyRepository.findById(studyId).orElseThrow(() -> new StudyException(NOT_FOUND_STUDY));
    }

}
