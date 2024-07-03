package doore.study.application;

import static doore.member.domain.StudyRoleType.ROLE_스터디장;
import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;
import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER_ROLE_IN_STUDY;
import static doore.member.exception.MemberExceptionType.UNAUTHORIZED;
import static doore.study.exception.StudyExceptionType.INVALID_ENDDATE;
import static doore.study.exception.StudyExceptionType.NOT_FOUND_STATUS;
import static doore.study.exception.StudyExceptionType.NOT_FOUND_STUDY;
import static doore.team.exception.TeamExceptionType.NOT_FOUND_TEAM;

import doore.member.domain.StudyRole;
import doore.member.domain.repository.MemberRepository;
import doore.member.domain.repository.StudyRoleRepository;
import doore.member.exception.MemberException;
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
import doore.team.domain.TeamRepository;
import doore.team.exception.TeamException;
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
    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;
    private final StudyRoleRepository studyRoleRepository;
    private final CurriculumItemRepository curriculumItemRepository;
    private final ParticipantCurriculumItemRepository participantCurriculumItemRepository;
    private final ParticipantCommandService participantCommandService;

    public void createStudy(final StudyCreateRequest request, final Long teamId, final Long memberId) {
        validateExistMember(memberId);
        validateExistTeam(teamId);
        checkEndDateValid(request.startDate(), request.endDate());
        final Study study = studyRepository.save(request.toStudy(teamId));
        studyRoleRepository.save(StudyRole.builder()
                .studyRoleType(ROLE_스터디장)
                .studyId(study.getId())
                .memberId(memberId)
                .build());
        saveParticipant(study.getId(), memberId, memberId);
    }

    private void saveParticipant(final Long studyId, final Long memberId, final Long studyLeaderId) {
        participantCommandService.saveParticipant(studyId,memberId,studyLeaderId);
    }

    private void checkEndDateValid(final LocalDate startDate, final LocalDate endDate) {
        if (endDate != null && startDate.isAfter(endDate)) {
            throw new StudyException(INVALID_ENDDATE);
        }
    }

    public void deleteStudy(final Long studyId, final Long memberId) {
        validateExistStudyLeader(studyId, memberId);
        validateExistStudy(studyId);

        final List<CurriculumItem> curriculumItems = curriculumItemRepository.findAllByStudyId(studyId);
        final List<Long> curriculumItemIds = curriculumItems.stream()
                .map(CurriculumItem::getId)
                .toList();

        curriculumItems.forEach(CurriculumItem::delete);

        curriculumItemIds.forEach(curriculumItemId -> {
            final List<ParticipantCurriculumItem> items = participantCurriculumItemRepository.findAllByCurriculumItemId(
                    curriculumItemId);
            items.forEach(ParticipantCurriculumItem::delete);
        });

        studyRepository.deleteById(studyId);
    }

    public void updateStudy(final StudyUpdateRequest request, final Long studyId, final Long memberId) {
        validateExistStudyLeader(studyId, memberId);
        final Study study = validateExistStudy(studyId);
        study.update(request.name(), request.description(), request.startDate(), request.endDate(), request.status());
    }

    public void terminateStudy(final Long studyId, final Long memberId) {
        validateExistStudyLeader(studyId, memberId);
        final Study study = validateExistStudy(studyId);
        study.terminate();
    }

    public void changeStudyStatus(final String status, final Long studyId, final Long memberId) {
        validateExistStudyLeader(studyId, memberId);
        final Study study = validateExistStudy(studyId);
        try {
            final StudyStatus changedStatus = StudyStatus.valueOf(status);
            study.changeStatus(changedStatus);
        } catch (final IllegalArgumentException e) {
            throw new StudyException(NOT_FOUND_STATUS);
        }
    }

    private void validateExistTeam(final Long teamId) {
        teamRepository.findById(teamId).orElseThrow(() -> new TeamException(NOT_FOUND_TEAM));
    }

    private Study validateExistStudy(final Long studyId) {
        return studyRepository.findById(studyId).orElseThrow(() -> new StudyException(NOT_FOUND_STUDY));
    }

    private void validateExistMember(final Long memberId) {
        memberRepository.findById(memberId).orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
    }

    private void validateExistStudyLeader(final Long studyId, final Long memberId) {
        final StudyRole studyRole = studyRoleRepository.findStudyRoleByStudyIdAndMemberId(studyId, memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ROLE_IN_STUDY));
        if (!studyRole.getStudyRoleType().equals(ROLE_스터디장)) {
            throw new MemberException(UNAUTHORIZED);
        }
    }
}
