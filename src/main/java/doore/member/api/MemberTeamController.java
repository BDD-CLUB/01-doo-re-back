package doore.member.api;

import doore.member.application.MemberTeamQueryService;
import doore.member.application.dto.response.MemberResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class MemberTeamController {
    private final MemberTeamQueryService memberTeamQueryService;

    @GetMapping("/teams/{teamId}/members")
    public ResponseEntity<List<MemberResponse>> getMemberTeam(@PathVariable Long teamId,
                                                              @RequestParam(value = "keyword", required = false) final String keyword) {
        final List<MemberResponse> memberResponses = memberTeamQueryService.findMemberTeams(teamId, keyword);
        return ResponseEntity.ok(memberResponses);
    }
}
