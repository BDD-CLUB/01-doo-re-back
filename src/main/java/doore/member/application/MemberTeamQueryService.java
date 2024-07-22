package doore.member.application;

import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;

import doore.member.application.convenience.TeamRoleValidateAccessPermission;
import doore.member.application.dto.response.TeamMemberResponse;
import doore.member.domain.Member;
import doore.member.domain.MemberTeam;
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
    
    private final TeamRoleValidateAccessPermission teamRoleValidateAccessPermission;

    public List<TeamMemberResponse> findMemberTeams(final Long teamId, final String keyword, final Long memberId) {
        teamRoleValidateAccessPermission.validateExistMemberTeam(teamId, memberId);
        if (keyword == null || keyword.isBlank()) {
            return findAllMemberOfTeam(teamId);
        }

        final List<Member> members = memberTeamRepository.findAllByTeamIdAndKeyword(teamId, keyword)
                .stream()
                .map(MemberTeam::getMember)
                .sorted(Comparator.comparing(Member::getName))
                .sorted(Comparator.comparing(member -> member.getName().length()))
                .toList();

        final Map<Member, TeamRoleType> roleOfMembers = getRoleOfMember(teamId, members);
        return TeamMemberResponse.of(members, roleOfMembers);
    }

    private List<TeamMemberResponse> findAllMemberOfTeam(final Long teamId) {
        final List<Member> members = memberTeamRepository.findAllByTeamId(teamId)
                .stream()
                .map(MemberTeam::getMember)
                .sorted(Comparator.comparing(Member::getName))
                .collect(Collectors.toList());

        final Map<Member, TeamRoleType> roleOfMembers = getRoleOfMember(teamId, members);
        return TeamMemberResponse.of(members, roleOfMembers);
    }

    private Map<Member, TeamRoleType> getRoleOfMember(final Long teamId, final List<Member> members) {
        return members.stream()
                .collect(Collectors.toMap(
                        member -> member,
                        member -> teamRoleRepository.findTeamRoleByTeamIdAndMemberId(teamId, member.getId())
                                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER))
                                .getTeamRoleType()
                ));
    }
}
