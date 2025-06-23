package doore.member.application;

import doore.file.application.S3ImageFileService;
import doore.login.application.dto.response.GoogleAccountProfileResponse;
import doore.member.application.convenience.MemberTeamConvenience;
import doore.member.application.convenience.MemberValidateAccessPermission;
import doore.member.application.convenience.StudyRoleConvenience;
import doore.member.application.convenience.StudyRoleValidateAccessPermission;
import doore.member.application.convenience.TeamRoleConvenience;
import doore.member.application.convenience.TeamRoleValidateAccessPermission;
import doore.member.application.dto.request.MyPageUpdateRequest;
import doore.member.domain.Member;
import doore.member.domain.StudyRole;
import doore.member.domain.TeamRole;
import doore.member.domain.repository.MemberRepository;
import doore.study.application.convenience.ParticipantConvenience;
import doore.study.application.convenience.StudyValidateAccessPermission;
import doore.team.application.convenience.TeamConvenience;
import doore.team.application.convenience.TeamValidateAccessPermission;
import doore.team.domain.Team;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberCommandService {

    private final MemberRepository memberRepository;

    private final TeamConvenience teamConvenience;
    private final TeamRoleConvenience teamRoleConvenience;
    private final MemberTeamConvenience memberTeamConvenience;
    private final ParticipantConvenience participantConvenience;
    private final StudyRoleConvenience studyRoleConvenience;

    private final S3ImageFileService s3ImageFileService;

    private final TeamRoleValidateAccessPermission teamRoleValidateAccessPermission;
    private final StudyRoleValidateAccessPermission studyRoleValidateAccessPermission;
    private final TeamValidateAccessPermission teamValidateAccessPermission;
    private final StudyValidateAccessPermission studyValidateAccessPermission;
    private final MemberValidateAccessPermission memberValidateAccessPermission;

    private static final String DEFAULT_IMAGE_URL = "TEMP_URL";

    // TODO: 1/23/24 추후 소셜 로그인 플랫폼이 늘어나는 경우의 확장성 관련해서 논의
    public Member findOrCreateMemberBy(final GoogleAccountProfileResponse profile) {
        return memberRepository.findByGoogleId(profile.id())
                .orElseGet(() -> memberRepository.save(
                        Member.builder()
                                .name(profile.name())
                                .googleId(profile.id())
                                .email(profile.email())
                                .imageUrl(profile.picture())
                                .build()));
    }

    public void transferTeamLeader(final Long teamId, final Long newTeamLeaderId, final Long memberId) {
        memberValidateAccessPermission.validateExistMember(newTeamLeaderId);
        teamValidateAccessPermission.validateExistTeam(teamId);

        final TeamRole checkTeamLeader = teamRoleValidateAccessPermission.getValidateExistTeamLeader(teamId, memberId);
        checkTeamLeader.updatePreviousTeamLeaderRole();

        final TeamRole teamRole = teamRoleValidateAccessPermission.getValidateExistMemberTeam(teamId, newTeamLeaderId);
        teamRole.updateTeamLeaderRole();
    }

    public void transferStudyLeader(final Long studyId, final Long newStudyLeaderId, final Long memberId) {
        memberValidateAccessPermission.validateExistMember(newStudyLeaderId);
        studyValidateAccessPermission.validateExistStudy(studyId);

        final StudyRole checkStudyLeader = studyRoleValidateAccessPermission.getValidateExistStudyLeader(studyId,
                memberId);
        checkStudyLeader.updatePreviousStudyLeaderRole();

        final StudyRole studyRole = studyRoleValidateAccessPermission.getValidateExistParticipant(studyId,
                newStudyLeaderId);
        studyRole.updateStudyLeaderRole();
    }

    public void deleteMember(final Long memberId) {
        final Member member = memberValidateAccessPermission.getValidateExistMember(memberId);
        List<Team> teams = teamConvenience.findAllByMemberId(memberId);
        teamRoleConvenience.isTeamLeader(teams, memberId);

        deleteAboutTeam(memberId);
        deleteAboutStudy(memberId);

        memberRepository.delete(member);
    }

    public void updateMyPage(final MyPageUpdateRequest request, final Long tokenMemberId) {
        final Member member = memberValidateAccessPermission.getValidateExistMember(tokenMemberId);
        member.updateMyPage(request.name());
    }

    public void updateMyPageImage(final MultipartFile file, final Long tokenMemberId) {
        final Member member = memberValidateAccessPermission.getValidateExistMember(tokenMemberId);
        checkHasImageAndDelete(member);

        final String newImageUrl = s3ImageFileService.upload(file);
        member.updateImageUrl(newImageUrl);
    }

    public void deleteMyPageImage(final Long tokenMemberId) {
        final Member member = memberValidateAccessPermission.getValidateExistMember(tokenMemberId);
        checkHasImageAndDelete(member);

        member.updateImageUrl(DEFAULT_IMAGE_URL);
    }

    private void checkHasImageAndDelete(final Member member) {
        if (member.hasImage()) {
            s3ImageFileService.deleteFile(member.getImageUrl());
        }
    }

    private void deleteAboutTeam(final Long memberId) {
        memberTeamConvenience.deleteAllMemberTeams(memberId);
        teamRoleConvenience.deleteAllTeamRoles(memberId);
    }

    private void deleteAboutStudy(final Long memberId) {
        participantConvenience.deleteAllParticipantsByMemberId(memberId);
        studyRoleConvenience.deleteAllStudyRoles(memberId);
    }

}
