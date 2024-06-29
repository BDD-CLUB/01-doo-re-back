package doore.member.api;

import doore.member.application.MemberTeamCommandService;
import doore.member.application.MemberTeamQueryService;
import doore.member.application.dto.response.TeamMemberResponse;
import doore.member.domain.Member;
import doore.resolver.LoginMember;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class MemberTeamController {
    private final MemberTeamQueryService memberTeamQueryService;
    private final MemberTeamCommandService memberTeamCommandService;

    @GetMapping("/teams/{teamId}/members") // 팀원 & 팀장
    public ResponseEntity<List<TeamMemberResponse>> getMemberTeam(@PathVariable final Long teamId,
                                                                  @RequestParam(value = "keyword", required = false) final String keyword,
                                                                  @LoginMember final Member member) {
        final List<TeamMemberResponse> memberResponses = memberTeamQueryService.findMemberTeams(teamId, keyword, member.getId());
        return ResponseEntity.ok(memberResponses);
    }

    @DeleteMapping("/teams/{teamId}/members/{deleteMemberId}") //팀장
    public ResponseEntity<Void> deleteMemberTeam(@PathVariable final Long teamId, @PathVariable final Long deleteMemberId,
                                                 @LoginMember final Member member) {
        memberTeamCommandService.deleteMemberTeam(teamId, deleteMemberId, member.getId());
        return ResponseEntity.noContent().build();
    }
}
