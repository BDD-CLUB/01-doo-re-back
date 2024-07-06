package doore.member.application;

import static doore.member.domain.TeamRoleType.ROLE_팀원;
import static doore.member.domain.TeamRoleType.ROLE_팀장;
import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;
import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER_ROLE_IN_TEAM;
import static doore.member.exception.MemberExceptionType.UNAUTHORIZED;

import doore.member.application.dto.response.TeamMemberResponse;
import doore.member.domain.Member;
import doore.member.domain.MemberTeam;
import doore.member.domain.TeamRole;
import doore.member.domain.TeamRoleType;
import doore.member.domain.repository.MemberTeamRepository;
import doore.member.domain.repository.TeamRoleRepository;
import doore.member.exception.MemberException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberTeamQueryService {

    private final MemberTeamRepository memberTeamRepository;
    private final TeamRoleRepository teamRoleRepository;

    public List<TeamMemberResponse> findMemberTeams(final Long teamId, final String keyword, final Long memberId) {
        validateExistTeamLeaderAndTeamMember(teamId, memberId);
        if (keyword == null || keyword.isBlank()) {
            return findAllMemberOfTeam(teamId);
        }

        final List<Member> members = memberTeamRepository.findAllByTeamIdAndKeyword(teamId, keyword)
                .stream()
                .map(MemberTeam::getMember)
                .sorted(Comparator.comparing(Member::getName))
                .sorted(Comparator.comparing(member -> member.getName().length()))
                .toList();

        final Map<Member, TeamRoleType> roleOfMembers = getRoleOfMember(members);
        return TeamMemberResponse.of(members, roleOfMembers);
    }

    private List<TeamMemberResponse> findAllMemberOfTeam(final Long teamId) {
        final List<Member> members = memberTeamRepository.findAllByTeamId(teamId)
                .stream()
                .map(MemberTeam::getMember)
                .sorted(Comparator.comparing(Member::getName))
                .collect(Collectors.toList());

        final Map<Member, TeamRoleType> roleOfMembers = getRoleOfMember(members);
        return TeamMemberResponse.of(members, roleOfMembers);
    }

    private Map<Member, TeamRoleType> getRoleOfMember(final List<Member> members) {
        return members.stream()
                .collect(Collectors.toMap(
                        member -> member,
                        member -> teamRoleRepository.findTeamRoleByMemberId(member.getId())
                                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER))
                                .getTeamRoleType()
                ));
    }

    private void validateExistTeamLeaderAndTeamMember(final Long teamId, final Long memberId) {
        final TeamRole teamRole = teamRoleRepository.findTeamRoleByTeamIdAndMemberId(teamId, memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ROLE_IN_TEAM));
        if (!(teamRole.getTeamRoleType().equals(ROLE_팀장) || teamRole.getTeamRoleType().equals(ROLE_팀원))){
            throw new MemberException(UNAUTHORIZED);
        }
    }
}
