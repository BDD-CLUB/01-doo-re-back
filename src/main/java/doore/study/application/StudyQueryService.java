package doore.study.application;

import doore.document.application.convenience.DocumentConvenience;
import doore.member.application.convenience.MemberConvenience;
import doore.member.application.convenience.StudyRoleConvenience;
import doore.member.domain.Participant;
import doore.member.domain.repository.ParticipantRepository;
import doore.study.application.convenience.StudyValidateAccessPermission;
import doore.study.application.dto.response.StudyRankResponse;
import doore.study.application.dto.response.StudyReferenceResponse;
import doore.study.application.dto.response.StudyResponse;
import doore.study.domain.Study;
import doore.study.domain.repository.CurriculumItemRepository;
import doore.study.domain.repository.ParticipantCurriculumItemRepository;
import doore.study.domain.repository.StudyRepository;
import doore.team.application.convenience.TeamValidateAccessPermission;
import doore.team.domain.Team;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyQueryService {
    private final StudyRepository studyRepository;
    private final ParticipantRepository participantRepository;
    private final ParticipantCurriculumItemRepository participantCurriculumItemRepository;
    private final CurriculumItemRepository curriculumItemRepository;

    private final StudyRoleConvenience studyRoleConvenience;
    private final MemberConvenience memberConvenience;
    private final DocumentConvenience documentConvenience;

    private final TeamValidateAccessPermission teamValidateAccessPermission;
    private final StudyValidateAccessPermission studyValidateAccessPermission;

    public StudyResponse getStudy(final Long studyId) {
        final Study study = studyValidateAccessPermission.getValidateExistStudy(studyId);
        final Long studyLeaderId = studyRoleConvenience.findStudyLeaderId(study.getId());
        final Team team = teamValidateAccessPermission.getValidateExistTeam(study.getTeamId());
        final long studyProgressRatio = checkStudyProgressRatio(studyId);

        return StudyResponse.of(study, team, studyProgressRatio, studyLeaderId);
    }

    public List<StudyReferenceResponse> getMyStudies(final Long memberId, final Long tokenMemberId) {
        memberConvenience.checkSameMemberIdAndTokenMemberId(memberId, tokenMemberId);

        final List<Participant> participants = participantRepository.findByMemberIdAndIsDeletedFalse(memberId);
        final List<Long> studyIds = participants.stream()
                .map(Participant::getStudyId)
                .toList();
        final List<Study> studies = studyRepository.findAllById(studyIds);

        return studies.stream()
                .map(study -> StudyReferenceResponse.of(study, checkStudyProgressRatio(study.getId())))
                .toList();
    }

    public Page<StudyRankResponse> getTeamStudies(final Long teamId, final Pageable pageable) {
        final List<Study> studies = studyRepository.findAllByTeamId(teamId);
        final List<StudyRankResponse> sortedStudyList = studies.stream()
                .map(this::convertStudyToStudyRankResponse)
                .sorted(Comparator.comparing(
                                (StudyRankResponse r) -> r.studyReferenceResponse().status().getOrder())
                        .thenComparing(StudyRankResponse::point, Comparator.reverseOrder()))
                .toList();

        final int start = (int) pageable.getOffset();
        final int end = Math.min(start + pageable.getPageSize(), sortedStudyList.size());
        final List<StudyRankResponse> pagedList = sortedStudyList.subList(start, end);

        return new PageImpl<>(pagedList, pageable, sortedStudyList.size());
    }

    private int checkStudyProgressRatio(final Long studyId) {
        final List<Long> curriculumItemIds = curriculumItemRepository.findIdsByStudyId(studyId);
        final long totalCurriculumItems = participantCurriculumItemRepository.countByCurriculumItemIdIn(
                curriculumItemIds);
        final long checkedTrueCurriculumItems = participantCurriculumItemRepository.countByCurriculumItemIdInAndIsCheckedTrue(
                curriculumItemIds);
        return (int)(totalCurriculumItems > 0 ? (checkedTrueCurriculumItems * 100) / totalCurriculumItems : 0);
    }

    private int calculatePoint(final Study study, final int progressRatio) {
        final long documentCount = documentConvenience.countByGroupId(study.getId());
        return (int)(progressRatio + documentCount);
    }

    private StudyRankResponse convertStudyToStudyRankResponse(final Study study) {
        final int checkStudyProgressRatio = checkStudyProgressRatio(study.getId());
        final StudyReferenceResponse studyReferenceResponse = StudyReferenceResponse.of(study,
                checkStudyProgressRatio(study.getId()));
        final int point = calculatePoint(study, checkStudyProgressRatio);
        return new StudyRankResponse(point, studyReferenceResponse);
    }
}
