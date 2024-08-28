package doore.study.application;

import static doore.member.exception.MemberExceptionType.UNAUTHORIZED;
import static doore.study.exception.StudyExceptionType.NOT_FOUND_STUDY;
import static doore.team.exception.TeamExceptionType.NOT_FOUND_TEAM;

import doore.member.application.convenience.StudyRoleConvenience;
import doore.member.domain.Participant;
import doore.member.domain.repository.MemberRepository;
import doore.member.domain.repository.ParticipantRepository;
import doore.member.domain.repository.StudyRoleRepository;
import doore.member.exception.MemberException;
import doore.study.application.convenience.StudyAuthorization;
import doore.study.application.dto.response.StudyRankResponse;
import doore.study.application.dto.response.StudyReferenceResponse;
import doore.study.application.dto.response.StudyResponse;
import doore.study.domain.Study;
import doore.study.domain.repository.CurriculumItemRepository;
import doore.study.domain.repository.ParticipantCurriculumItemRepository;
import doore.study.domain.repository.StudyRepository;
import doore.study.exception.StudyException;
import doore.team.application.convenience.TeamAuthorization;
import doore.team.domain.Team;
import doore.team.domain.TeamRepository;
import doore.team.exception.TeamException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    private final StudyAuthorization studyAuthorization;
    private final StudyRoleConvenience studyRoleConvenience;
    private final TeamAuthorization teamAuthorization;

    public StudyResponse findStudyById(final Long studyId) {
        final Study study = studyAuthorization.getStudyOrThrow(studyId);
        final Long studyLeaderId = studyRoleConvenience.findStudyLeaderId(study.getId());
        final Team team = teamAuthorization.getTeamOrThrow(study.getTeamId());
        final long studyProgressRatio = checkStudyProgressRatio(studyId);

        return StudyResponse.of(study, team, studyProgressRatio, studyLeaderId);
    }

    public List<StudyReferenceResponse> findMyStudies(final Long memberId, final Long tokenMemberId) {
        checkSameMemberIdAndTokenMemberId(memberId, tokenMemberId);

        final List<Participant> participants = participantRepository.findByMemberId(memberId);
        final List<Long> studyIds = participants.stream()
                .map(Participant::getStudyId)
                .toList();
        final List<Study> studies = studyRepository.findAllById(studyIds);

        return studies.stream()
                .map(study -> StudyReferenceResponse.of(study, checkStudyProgressRatio(study.getId())))
                .toList();
    }

    private void checkSameMemberIdAndTokenMemberId(final Long memberId, final Long tokenMemberId) {
        if (!memberId.equals(tokenMemberId)) {
            throw new MemberException(UNAUTHORIZED);
        }
    }

    private long checkStudyProgressRatio(final Long studyId) {
        final List<Long> curriculumItemIds = curriculumItemRepository.findIdsByStudyId(studyId);
        final long totalCurriculumItems = participantCurriculumItemRepository.countByCurriculumItemIdIn(
                curriculumItemIds);
        final long checkedTrueCurriculumItems = participantCurriculumItemRepository.countByCurriculumItemIdInAndIsCheckedTrue(
                curriculumItemIds);
        return totalCurriculumItems > 0 ? (checkedTrueCurriculumItems * 100) / totalCurriculumItems : 0;
    }
    
    public Page<StudyRankResponse> getTeamStudies(final Long teamId, final Pageable pageable) {
        return studyRepository.findAllByTeamId(teamId, pageable)
                .map(this::convertStudyToStudyRankResponse);
        //todo: (24.07.09) point 기반 정렬 로직 추가;
    }

    private StudyRankResponse convertStudyToStudyRankResponse(final Study study) {
        StudyReferenceResponse studyReferenceResponse = StudyReferenceResponse.of(study,
                checkStudyProgressRatio(study.getId()));
        return new StudyRankResponse(calculatePoint(study), studyReferenceResponse);
    }

    private int calculatePoint(final Study study) {
        //todo: (24.07.09) 스터디 점수 계산 방식에 대해 논의 후 로직 추가 (디스커션 #163 참고)
        return 0;
    }
}
