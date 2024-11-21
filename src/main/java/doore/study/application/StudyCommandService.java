package doore.study.application;

import static doore.study.exception.StudyExceptionType.INVALID_ENDDATE;
import static doore.study.exception.StudyExceptionType.NOT_FOUND_STATUS;

import doore.member.application.convenience.MemberValidateAccessPermission;
import doore.member.application.convenience.StudyRoleConvenience;
import doore.member.application.convenience.StudyRoleValidateAccessPermission;
import doore.member.application.convenience.TeamRoleValidateAccessPermission;
import doore.member.domain.Member;
import doore.study.application.convenience.ParticipantConvenience;
import doore.study.application.convenience.StudyValidateAccessPermission;
import doore.study.application.dto.request.StudyCreateRequest;
import doore.study.application.dto.request.StudyUpdateRequest;
import doore.study.domain.CurriculumItem;
import doore.study.domain.ParticipantCurriculumItem;
import doore.study.domain.Study;
import doore.study.domain.StudyStatus;
import doore.study.domain.repository.CurriculumItemRepository;
import doore.study.domain.repository.ParticipantCurriculumItemRepository;
import doore.study.domain.repository.StudyRepository;
import doore.study.exception.StudyException;
import doore.team.application.convenience.TeamValidateAccessPermission;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyCommandService {
    private final StudyRepository studyRepository;
    private final CurriculumItemRepository curriculumItemRepository;
    private final ParticipantCurriculumItemRepository participantCurriculumItemRepository;

    private final StudyRoleConvenience studyRoleConvenience;
    private final ParticipantConvenience participantConvenience;

    private final StudyValidateAccessPermission studyValidateAccessPermission;
    private final TeamValidateAccessPermission teamValidateAccessPermission;
    private final MemberValidateAccessPermission memberValidateAccessPermission;
    private final TeamRoleValidateAccessPermission teamRoleValidateAccessPermission;
    private final StudyRoleValidateAccessPermission studyRoleValidateAccessPermission;

    public void createStudy(final StudyCreateRequest request, final Long teamId, final Long memberId) {
        teamRoleValidateAccessPermission.validateExistMemberTeam(teamId, memberId);
        teamValidateAccessPermission.validateExistTeam(teamId);
        checkEndDateValid(request.startDate(), request.endDate());

        final Member member = memberValidateAccessPermission.getValidateExistMember(memberId);
        final Study study = studyRepository.save(request.toStudy(teamId));

        studyRoleConvenience.assignStudyLeaderRole(study.getId(), memberId);
        participantConvenience.assignParticipant(study.getId(), member);
        studyRoleConvenience.assignParticipantRole(study.getId(), memberId, memberId);
    }

    public void deleteStudy(final Long studyId, final Long memberId) {
        studyRoleValidateAccessPermission.validateExistStudyLeader(studyId, memberId);
        studyValidateAccessPermission.validateExistStudy(studyId);

        deleteCurriculumItemAndParticipantCurriculumItem(studyId);
        participantConvenience.deleteParticipant(studyId);
        studyRepository.deleteById(studyId);
    }

    public void updateStudy(final StudyUpdateRequest request, final Long studyId, final Long memberId) {
        studyRoleValidateAccessPermission.validateExistStudyLeader(studyId, memberId);
        checkEndDateValid(request.startDate(), request.endDate());
        final Study study = studyValidateAccessPermission.getValidateExistStudy(studyId);
        study.update(request.name(), request.description(), request.startDate(), request.endDate(), request.status());
    }

    public void terminateStudy(final Long studyId, final Long memberId) {
        studyRoleValidateAccessPermission.validateExistStudyLeader(studyId, memberId);
        final Study study = studyValidateAccessPermission.getValidateExistStudy(studyId);
        study.terminate();
    }

    public void changeStudyStatus(final String status, final Long studyId, final Long memberId) {
        studyRoleValidateAccessPermission.validateExistStudyLeader(studyId, memberId);
        final Study study = studyValidateAccessPermission.getValidateExistStudy(studyId);
        try {
            final StudyStatus changedStatus = StudyStatus.valueOf(status);
            study.changeStatus(changedStatus);
        } catch (final IllegalArgumentException e) {
            throw new StudyException(NOT_FOUND_STATUS);
        }
    }

    private void checkEndDateValid(final LocalDate startDate, final LocalDate endDate) {
        if (endDate != null && startDate.isAfter(endDate)) {
            throw new StudyException(INVALID_ENDDATE);
        }
    }

    private void deleteCurriculumItemAndParticipantCurriculumItem(final Long studyId) {
        final List<CurriculumItem> curriculumItems = curriculumItemRepository.findAllByStudyId(studyId);
        final List<Long> curriculumItemIds = curriculumItems.stream()
                .map(CurriculumItem::getId)
                .toList();

        curriculumItems.forEach(CurriculumItem::delete); // todo: 수료증 개발 시 delete 로직 확인 필요

        curriculumItemIds.forEach(curriculumItemId -> {
            final List<ParticipantCurriculumItem> items = participantCurriculumItemRepository.findAllByCurriculumItemId(
                    curriculumItemId);
            items.forEach(ParticipantCurriculumItem::delete); // todo: 수료증 개발 시 delete 로직 확인 필요
        });
    }
}
