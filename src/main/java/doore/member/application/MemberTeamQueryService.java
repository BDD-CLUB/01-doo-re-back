package doore.member.application;

import doore.member.application.dto.response.MemberResponse;
import doore.member.domain.Member;
import doore.member.domain.MemberTeam;
import doore.member.domain.repository.MemberTeamRepository;
import java.util.Comparator;
import java.util.HashMap;
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

    public List<MemberResponse> findMemberTeams(final Long teamId, final String keyword) {
        if (keyword.isBlank()) {
            return findAllMemberOfTeam(teamId);
        }

        final List<Member> members = memberTeamRepository.findAllByTeamIdAndKeyword(teamId, keyword)
                .stream()
                .map(MemberTeam::getMember)
                .sorted(Comparator.comparing(Member::getName))
                .sorted(Comparator.comparing(member -> member.getName().length()))
                .toList();
        // 추후 role 구현 후 수정 예정
        final Map<Member, String> roleOfMembers = new HashMap<>();
        members.stream()
                .forEach(member -> roleOfMembers.put(member, "팀원"));
        return MemberResponse.of(members, roleOfMembers);
    }

    private List<MemberResponse> findAllMemberOfTeam(final Long teamId) {
        final List<Member> members = memberTeamRepository.findAllByTeamId(teamId)
                .stream()
                .map(MemberTeam::getMember)
                .sorted(Comparator.comparing(Member::getName))
                .collect(Collectors.toList());
        // 추후 role 구현 후 수정 예정
        final Map<Member, String> roleOfMembers = new HashMap<>();
        members.stream()
                .forEach(member -> roleOfMembers.put(member, "팀원"));
        return MemberResponse.of(members, roleOfMembers);
    }
}
