package doore.member.application;

import static doore.member.domain.StudyRoleType.ROLE_스터디장;
import static doore.member.domain.TeamRoleType.ROLE_팀장;
import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;
import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER_ROLE_IN_STUDY;
import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER_ROLE_IN_TEAM;
import static doore.study.exception.StudyExceptionType.NOT_FOUND_STUDY;
import static doore.team.exception.TeamExceptionType.NOT_FOUND_TEAM;

import doore.login.application.dto.response.GoogleAccountProfileResponse;
import doore.member.domain.Member;
import doore.member.domain.StudyRole;
import doore.member.domain.TeamRole;
import doore.member.domain.repository.MemberRepository;
import doore.member.domain.repository.StudyRoleRepository;
import doore.member.domain.repository.TeamRoleRepository;
import doore.member.exception.MemberException;
import doore.study.domain.Study;
import doore.study.domain.repository.StudyRepository;
import doore.study.exception.StudyException;
import doore.team.domain.Team;
import doore.team.domain.TeamRepository;
import doore.team.exception.TeamException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberCommandService {

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final StudyRepository studyRepository;
    private final TeamRoleRepository teamRoleRepository;
    private final StudyRoleRepository studyRoleRepository;


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

    public void transferTeamMaster(Long teamId, Long memberId) {
        validMember(memberId);
        validTeam(teamId);

        TeamRole previousTeamMasterRole = teamRoleRepository.findTeamRoleByTeamIdAndTeamRoleType(teamId, ROLE_팀장)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ROLE_IN_TEAM));
        previousTeamMasterRole.updatePreviousTeamMaster();

        TeamRole teamRole = teamRoleRepository.findTeamRoleByTeamIdAndMemberId(teamId, memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ROLE_IN_TEAM));
        teamRole.updateTeamMaster();
    }

    public void transferStudyMaster(Long studyId, Long memberId) {
        validMember(memberId);
        validStudy(studyId);

        StudyRole previousStudyMasterRole = studyRoleRepository.findStudyRoleByStudyIdAndStudyRoleType(studyId,
                ROLE_스터디장).orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ROLE_IN_STUDY));
        previousStudyMasterRole.updatePreviousStudyMaster();

        StudyRole studyRole = studyRoleRepository.findStudyRoleByStudyIdAndMemberId(studyId, memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ROLE_IN_STUDY));
        studyRole.updateStudyMaster();
    }

    private Member validMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
    }

    private Team validTeam(Long teamId) {
        return teamRepository.findById(teamId).orElseThrow(() -> new TeamException(NOT_FOUND_TEAM));
    }

    private Study validStudy(Long studyId) {
        return studyRepository.findById(studyId).orElseThrow(() -> new StudyException(NOT_FOUND_STUDY));
    }
}
