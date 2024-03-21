package doore.team.api;

import doore.team.application.TeamCommandService;
import doore.team.application.TeamQueryService;
import doore.team.application.dto.request.TeamCreateRequest;
import doore.team.application.dto.request.TeamInviteCodeRequest;
import doore.team.application.dto.request.TeamUpdateRequest;
import doore.team.application.dto.response.TeamInviteCodeResponse;
import doore.team.application.dto.response.TeamReferenceResponse;
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

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Void> createTeam(
            @Valid @RequestPart final TeamCreateRequest request,
            @RequestPart(required = false) final MultipartFile file
    ) {
        teamCommandService.createTeam(request, file);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{teamId}")
    public ResponseEntity<Void> updateTeam(
            @PathVariable final Long teamId,
            @RequestBody final TeamUpdateRequest request
    ) {
        teamCommandService.updateTeam(teamId, request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{teamId}/image")
    public ResponseEntity<Void> updateTeamImage(
            @PathVariable final Long teamId,
            @RequestPart(required = false) final MultipartFile file
    ) {
        teamCommandService.updateTeamImage(teamId, file);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<Void> deleteTeam(@PathVariable final Long teamId) {
        teamCommandService.deleteTeam(teamId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{teamId}/invite-code")
    public ResponseEntity<TeamInviteCodeResponse> generateTeamInviteCode(
            @PathVariable final Long teamId
    ) {
        final TeamInviteCodeResponse teamInviteCodeResponse = teamCommandService.generateTeamInviteCode(teamId);
        return ResponseEntity.ok(teamInviteCodeResponse);
    }

    @PostMapping("/{teamId}/join")
    public ResponseEntity<Void> joinTeam(
            @PathVariable final Long teamId,
            @Valid @RequestBody final TeamInviteCodeRequest request
    ) {
        teamCommandService.joinTeam(teamId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/members/{memberId}")
    public ResponseEntity<List<TeamReferenceResponse>> getMyTeams(@PathVariable final Long memberId) {
        // TODO: 3/22/24 토큰의 주인이 memberId와 동일인물인지 검증
        return ResponseEntity.ok(teamQueryService.findMyTeams(memberId));
    }
}
