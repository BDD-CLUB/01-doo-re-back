package doore.team.api;

import doore.member.domain.Member;
import doore.resolver.LoginMember;
import doore.team.application.TeamCommandService;
import doore.team.application.TeamQueryService;
import doore.team.application.dto.request.TeamCreateRequest;
import doore.team.application.dto.request.TeamInviteCodeRequest;
import doore.team.application.dto.request.TeamUpdateRequest;
import doore.team.application.dto.response.TeamInviteCodeResponse;
import doore.team.application.dto.response.TeamRankResponse;
import doore.team.application.dto.response.TeamReferenceResponse;
import doore.team.application.dto.response.TeamResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/teams")
public class TeamController {

    private final TeamCommandService teamCommandService;
    private final TeamQueryService teamQueryService;

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE}) // 회원
    public ResponseEntity<Void> createTeam(
            @Valid @RequestPart final TeamCreateRequest request,
            @RequestPart(required = false) final MultipartFile file,
            @LoginMember final Member member
    ) {
        teamCommandService.createTeam(request, file, member.getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{teamId}") // 팀장
    public ResponseEntity<Void> updateTeam(
            @PathVariable final Long teamId,
            @RequestBody final TeamUpdateRequest request,
            @LoginMember final Member member
    ) {
        teamCommandService.updateTeam(teamId, request, member.getId());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{teamId}/image") // 팀장
    public ResponseEntity<Void> updateTeamImage(
            @PathVariable final Long teamId,
            @RequestPart(required = false) final MultipartFile file,
            @LoginMember final Member member
    ) {
        teamCommandService.updateTeamImage(teamId, file, member.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{teamId}") // 팀장
    public ResponseEntity<Void> deleteTeam(@PathVariable final Long teamId, @LoginMember final Member member) {
        teamCommandService.deleteTeam(teamId, member.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{teamId}/invite-code") // 팀장
    public ResponseEntity<TeamInviteCodeResponse> generateTeamInviteCode(@PathVariable final Long teamId,
                                                                         @LoginMember final Member member) {
        final TeamInviteCodeResponse teamInviteCodeResponse = teamCommandService.generateTeamInviteCode(teamId, member.getId());
        return ResponseEntity.ok(teamInviteCodeResponse);
    }

    @PostMapping("/{teamId}/join") // 회원
    public ResponseEntity<Void> joinTeam(
            @PathVariable final Long teamId,
            @Valid @RequestBody final TeamInviteCodeRequest request,
            @LoginMember final Member member
    ) {
        teamCommandService.joinTeam(teamId, request, member.getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/members/{memberId}") // 회원
    public ResponseEntity<List<TeamReferenceResponse>> getMyTeams(@PathVariable final Long memberId,
                                                                  @LoginMember final Member member) {
        // TODO: 3/22/24 토큰의 주인이 memberId와 동일인물인지 검증 (2024/5/14 완료 -> 서비스에서 진행)
        return ResponseEntity.ok(teamQueryService.findMyTeams(memberId, member.getId()));
    }

    @GetMapping("/{teamId}") // 비회원
    public ResponseEntity<TeamResponse> getTeam(@PathVariable final Long teamId) {
        return ResponseEntity.ok(teamQueryService.findTeamByTeamId(teamId));
    }

    @GetMapping // 비회원
    public ResponseEntity<List<TeamRankResponse>> getTeams(
    ) {
        final List<TeamRankResponse> teamRankResponses = teamQueryService.getTeamRanks();
        return ResponseEntity.ok(teamRankResponses);
    }
}
