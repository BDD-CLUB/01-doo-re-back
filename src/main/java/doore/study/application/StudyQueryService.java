package doore.study.application;

import static doore.crop.exception.CropExceptionType.NOT_FOUND_CROP;
import static doore.member.domain.StudyRoleType.ROLE_스터디원;
import static doore.member.domain.StudyRoleType.ROLE_스터디장;
import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;
import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER_ROLE_IN_STUDY;
import static doore.member.exception.MemberExceptionType.UNAUTHORIZED;
import static doore.study.exception.StudyExceptionType.NOT_FOUND_STUDY;
import static doore.team.exception.TeamExceptionType.NOT_FOUND_TEAM;

import doore.crop.domain.Crop;
import doore.crop.domain.repository.CropRepository;
import doore.crop.exception.CropException;
import doore.member.domain.Participant;
import doore.member.domain.StudyRole;
import doore.member.domain.repository.MemberRepository;
import doore.member.domain.repository.ParticipantRepository;
import doore.member.domain.repository.StudyRoleRepository;
import doore.member.exception.MemberException;
import doore.study.application.dto.response.StudyReferenceResponse;
import doore.study.application.dto.response.StudyResponse;
import doore.study.domain.Study;
import doore.study.domain.repository.CurriculumItemRepository;
import doore.study.domain.repository.ParticipantCurriculumItemRepository;
import doore.study.domain.repository.StudyRepository;
import doore.study.exception.StudyException;
import doore.team.domain.Team;
import doore.team.domain.TeamRepository;
import doore.team.exception.TeamException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyQueryService {
    private final StudyRepository studyRepository;
    private final StudyRoleRepository studyRoleRepository;
    private final ParticipantRepository participantRepository;
    private final ParticipantCurriculumItemRepository participantCurriculumItemRepository;
    private final CurriculumItemRepository curriculumItemRepository;
    private final TeamRepository teamRepository;
    private final CropRepository cropRepository;
    private final MemberRepository memberRepository;

    public StudyResponse findStudyById(final Long studyId) {
        final Study study = studyRepository.findById(studyId).orElseThrow(() -> new StudyException(NOT_FOUND_STUDY));
        final Long studyLeaderId = studyRoleRepository.findLeaderIdByStudyId(study.getId());

        final Team team = teamRepository.findById(study.getTeamId())
                .orElseThrow(() -> new TeamException(NOT_FOUND_TEAM));
        final Crop crop = cropRepository.findById(study.getCropId())
                .orElseThrow(() -> new CropException(NOT_FOUND_CROP));
        final long studyProgressRatio = checkStudyProgressRatio(studyId);

        return StudyResponse.of(study, team, crop, studyProgressRatio, studyLeaderId);
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

    private void validateExistStudyLeaderAndParticipant(final Long memberId) {
        final StudyRole studyRole = studyRoleRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ROLE_IN_STUDY));
        if (!(studyRole.getStudyRoleType().equals(ROLE_스터디장) || studyRole.getStudyRoleType().equals(ROLE_스터디원))) {
            throw new MemberException(UNAUTHORIZED);
        }
    }

    private void validateExistMember(final Long memberId) {
        memberRepository.findById(memberId).orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
    }

    private long checkStudyProgressRatio(Long studyId) {
        final List<Long> curriculumItemIds = curriculumItemRepository.findIdsByStudyId(studyId);
        final long totalCurriculumItems = participantCurriculumItemRepository.countByCurriculumItemIdIn(
                curriculumItemIds);
        final long checkedTrueCurriculumItems = participantCurriculumItemRepository.countByCurriculumItemIdInAndIsCheckedTrue(
                curriculumItemIds);
        return totalCurriculumItems > 0 ? (checkedTrueCurriculumItems * 100) / totalCurriculumItems : 0;
    }
}
