package doore.study.application;

import static doore.crop.exception.CropExceptionType.NOT_FOUND_CROP;
import static doore.member.domain.StudyRoleType.ROLE_스터디원;
import static doore.member.domain.StudyRoleType.ROLE_스터디장;
import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;
import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER_ROLE_IN_STUDY;
import static doore.member.exception.MemberExceptionType.UNAUTHORIZED;
import static doore.study.exception.StudyExceptionType.NOT_FOUND_STUDY;
import static doore.team.exception.TeamExceptionType.NOT_FOUND_TEAM;
import static java.util.stream.Collectors.groupingBy;

import doore.crop.domain.Crop;
import doore.crop.domain.repository.CropRepository;
import doore.crop.exception.CropException;
import doore.member.domain.StudyRole;
import doore.member.domain.repository.MemberRepository;
import doore.member.domain.repository.StudyRoleRepository;
import doore.member.exception.MemberException;

import doore.study.application.dto.response.PersonalCurriculumItemResponse;
import doore.study.application.dto.response.PersonalStudyDetailResponse;
import doore.study.application.dto.response.CurriculumItemResponse;
import doore.study.application.dto.response.ParticipantCurriculumItemResponse;
import doore.study.application.dto.response.StudyDetailResponse;
import doore.study.application.dto.response.StudySimpleResponse;
import doore.study.domain.CurriculumItem;
import doore.study.domain.Study;
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
    private final StudyRoleRepository studyRoleRepository;
    private final TeamRepository teamRepository;
    private final CropRepository cropRepository;
    private final MemberRepository memberRepository;
    private final StudyDao studyDao;

    public StudyDetailResponse findStudyById(Long studyId, Long memberId) {
        validateExistStudyLeaderAndParticipant(memberId);
        Study study = getStudy(studyId);
        final Team team = teamRepository.findById(study.getTeamId())
                .orElseThrow(() -> new TeamException(NOT_FOUND_TEAM));
        final Crop crop = cropRepository.findById(study.getCropId())
                .orElseThrow(() -> new CropException(NOT_FOUND_CROP));
        return StudyDetailResponse.of(study, team, crop);
    }

    public PersonalStudyDetailResponse getPersonalStudyDetail(Long studyId, Long memberId) {
        validateExistMember(memberId);
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

    public List<StudySimpleResponse> findMyStudies(final Long memberId, final Long tokenMemberId) {
        checkSameMemberIdAndTokenMemberId(memberId, tokenMemberId);
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

    private void checkSameMemberIdAndTokenMemberId(final Long memberId, final Long tokenMemberId) {
        if (!memberId.equals(tokenMemberId)){
            throw new MemberException(UNAUTHORIZED);
        }
    }

    private void validateExistStudyLeaderAndParticipant(Long memberId) {
        StudyRole studyRole = studyRoleRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ROLE_IN_STUDY));
        if (!(studyRole.getStudyRoleType().equals(ROLE_스터디장) || studyRole.getStudyRoleType().equals(ROLE_스터디원))){
            throw new MemberException(UNAUTHORIZED);
        }
    }

    private void validateExistMember(Long memberId) {
        memberRepository.findById(memberId).orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
    }
}
