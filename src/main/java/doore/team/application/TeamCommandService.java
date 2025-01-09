package doore.team.application;

import static doore.team.exception.TeamExceptionType.EXPIRED_LINK;
import static doore.team.exception.TeamExceptionType.NOT_MATCH_LINK;

import doore.file.application.S3ImageFileService;
import doore.member.application.convenience.MemberTeamConvenience;
import doore.member.application.convenience.MemberValidateAccessPermission;
import doore.member.application.convenience.TeamRoleConvenience;
import doore.member.application.convenience.TeamRoleValidateAccessPermission;
import doore.member.domain.Member;
import doore.member.domain.MemberTeam;
import doore.member.domain.Participant;
import doore.member.domain.repository.MemberTeamRepository;
import doore.member.domain.repository.ParticipantRepository;
import doore.study.domain.CurriculumItem;
import doore.study.domain.ParticipantCurriculumItem;
import doore.study.domain.Study;
import doore.study.domain.repository.CurriculumItemRepository;
import doore.study.domain.repository.ParticipantCurriculumItemRepository;
import doore.study.domain.repository.StudyRepository;
import doore.team.application.convenience.TeamValidateAccessPermission;
import doore.team.application.dto.request.TeamCreateRequest;
import doore.team.application.dto.request.TeamInviteCodeRequest;
import doore.team.application.dto.request.TeamUpdateRequest;
import doore.team.application.dto.response.TeamInviteCodeResponse;
import doore.team.domain.Team;
import doore.team.domain.TeamRepository;
import doore.team.exception.TeamException;
import doore.util.RandomUtil;
import doore.util.RedisUtil;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class TeamCommandService {

    private final TeamRepository teamRepository;
    private final StudyRepository studyRepository;
    private final CurriculumItemRepository curriculumItemRepository;
    private final ParticipantCurriculumItemRepository participantCurriculumItemRepository;
    private final MemberTeamRepository memberTeamRepository;
    private final ParticipantRepository participantRepository;

    private final S3ImageFileService s3ImageFileService;
    private final RedisUtil redisUtil;

    private final TeamRoleConvenience teamRoleConvenience;
    private final MemberTeamConvenience memberTeamConvenience;
    private final TeamRoleValidateAccessPermission teamRoleValidateAccessPermission;
    private final TeamValidateAccessPermission teamValidateAccessPermission;
    private final MemberValidateAccessPermission memberValidateAccessPermission;

    private static final String INVITE_LINK_PREFIX = "teamId=%d";
    private static final String DEFAULT_IMAGE_URL = "https://doo-re-dev-bucket2.s3.ap-northeast-2.amazonaws.com/logo/logo.png";

    public void createTeam(final TeamCreateRequest request, final MultipartFile file, final Long memberId) {
        Member member = memberValidateAccessPermission.getValidateExistMember(memberId);
        final String imageUrl = (file != null && !file.isEmpty()) ? s3ImageFileService.upload(file) : DEFAULT_IMAGE_URL;

        try {
            final Team team = Team.builder()
                    .name(request.name())
                    .description(request.description())
                    .imageUrl(imageUrl)
                    .build();
            teamRepository.save(team);

            memberTeamConvenience.assignMemberTeam(member, team.getId());
            teamRoleConvenience.assignTeamLeaderRole(team.getId(), memberId);
        } catch (final Exception e) {
            s3ImageFileService.deleteFile(imageUrl);
        }
    }

    public void updateTeam(final Long teamId, final TeamUpdateRequest request, final Long memberId) {
        teamRoleValidateAccessPermission.validateExistTeamLeader(teamId, memberId);
        final Team team = teamValidateAccessPermission.getValidateExistTeam(teamId);
        team.update(request.name(), request.description());
    }

    public void updateTeamImage(final Long teamId, final MultipartFile file, final Long memberId) {
        teamRoleValidateAccessPermission.validateExistTeamLeader(teamId, memberId);
        final Team team = teamValidateAccessPermission.getValidateExistTeam(teamId);
        checkHasImageAndDelete(team);

        final String newImageUrl = s3ImageFileService.upload(file);
        team.updateImageUrl(newImageUrl);
    }

    public void deleteTeamImage(final Long teamId, final Long memberId) {
        final Team team = teamValidateAccessPermission.getValidateExistTeam(teamId);
        teamRoleValidateAccessPermission.validateExistTeamLeader(teamId, memberId);
        checkHasImageAndDelete(team);

        team.updateImageUrl(DEFAULT_IMAGE_URL);
    }

    public void deleteTeam(final Long teamId, final Long memberId) {
        teamRoleValidateAccessPermission.validateExistTeamLeader(teamId, memberId);
        final Team team = teamValidateAccessPermission.getValidateExistTeam(teamId);
        teamRepository.delete(team);
        checkHasImageAndDelete(team);

        deleteMemberTeamAndParticipant(teamId);
        deleteStudyAndCurriculumItemAndParticipantCurriculumItem(teamId);
    }

    public TeamInviteCodeResponse generateTeamInviteCode(final Long teamId, final Long memberId) {
        teamValidateAccessPermission.validateExistTeam(teamId);
        teamRoleValidateAccessPermission.validateExistTeamLeader(teamId, memberId);

        final Optional<String> link = redisUtil.getData(INVITE_LINK_PREFIX.formatted(teamId), String.class);
        if (link.isEmpty()) {
            final String randomCode = RandomUtil.generateRandomCode('0', 'z', 10);
            redisUtil.setDataExpire(INVITE_LINK_PREFIX.formatted(teamId), randomCode, RedisUtil.toTomorrow());
            return new TeamInviteCodeResponse(randomCode);
        }
        return new TeamInviteCodeResponse(link.get());
    }

    public void joinTeam(final Long teamId, final TeamInviteCodeRequest request, final Long memberId) {
        teamValidateAccessPermission.validateExistTeam(teamId);
        Member member = memberValidateAccessPermission.getValidateExistMember(memberId);

        final Optional<String> link = redisUtil.getData(INVITE_LINK_PREFIX.formatted(teamId), String.class);

        if (link.isPresent()) {
            validateMatchLink(link.get(), request.code());
            memberTeamConvenience.duplicateCheckTeamMember(teamId, memberId);
            teamRoleConvenience.assignTeamMemberRole(teamId, memberId);
            memberTeamConvenience.assignMemberTeam(member, teamId);
        } else {
            throw new TeamException(EXPIRED_LINK);
        }
    }

    private void validateMatchLink(final String link, final String userLink) {
        if (!link.equals(userLink)) {
            throw new TeamException(NOT_MATCH_LINK);
        }
    }

    private void deleteMemberTeamAndParticipant(final Long teamId) {
        final List<MemberTeam> memberTeams = memberTeamRepository.findAllByTeamId(teamId);
        memberTeamRepository.deleteAll(memberTeams);

        final List<Study> studies = studyRepository.findAllByTeamId(teamId);
        studies.forEach(study -> {
            final List<Participant> participants = participantRepository.findAllByStudyId(study.getId());
            participantRepository.deleteAll(participants);
        });
    }

    private void deleteStudyAndCurriculumItemAndParticipantCurriculumItem(final Long teamId) {
        final List<Study> studies = studyRepository.findAllByTeamId(teamId);

        studies.forEach(study -> {
            study.delete();
            final List<CurriculumItem> curriculumItems = curriculumItemRepository.findAllByStudyId(study.getId());

            curriculumItems.forEach(curriculumItem -> {
                curriculumItem.delete(); // todo: 수료증 개발 시 확인 필요
                final List<ParticipantCurriculumItem> participantCurriculumItems = participantCurriculumItemRepository.findAllByCurriculumItemId(
                        curriculumItem.getId());
                participantCurriculumItems.forEach(ParticipantCurriculumItem::delete); // todo: 수료증 개발 시 delete 로직 확인 필요
            });
        });
    }

    private void checkHasImageAndDelete(final Team team) {
        if (team.hasImage()) {
            s3ImageFileService.deleteFile(team.getImageUrl());
        }
    }
}
