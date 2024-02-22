package doore.study.application;

import static doore.study.exception.StudyExceptionType.NOT_FOUND_STUDY;

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
import doore.member.domain.Participant;
import doore.member.domain.repository.ParticipantRepository;
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
    private final ParticipantRepository participantRepository;

    public StudyDetailResponse findStudyById(Long studyId) {
        Study study = getStudy(studyId);
        List<CurriculumItemResponse> curriculumItemResponses = getListCurriculumItemResponse(study);
        StudyResponse studyResponse = toStudyResponse(study);

        return new StudyDetailResponse(studyResponse, curriculumItemResponses);
    }

    public List<Participant> findAllParticipants(Long studyId) {
        studyRepository.findById(studyId).orElseThrow(() -> new StudyException(NOT_FOUND_STUDY));
        return participantRepository.findAllByStudyId(studyId);
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
        return new StudyResponse(study.getId(), study.getName(), study.getDescription(),
                study.getStartDate(), study.getEndDate(), study.getStatus(), study.getIsDeleted(), study.getTeamId(),
                study.getCropId());
    }

    private Study getStudy(Long studyId) {
        return studyRepository.findById(studyId).orElseThrow(() -> new StudyException(NOT_FOUND_STUDY));
    }

}
