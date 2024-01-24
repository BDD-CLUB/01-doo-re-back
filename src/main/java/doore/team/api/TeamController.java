package doore.team.api;

import doore.team.application.TeamCommandService;
import doore.team.application.dto.request.TeamCreateRequest;
import doore.team.application.dto.request.TeamUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/teams")
public class TeamController {

    private final TeamCommandService teamCommandService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createTeam(@Valid @RequestBody final TeamCreateRequest request) {
        teamCommandService.createTeam(request);
    }

    @PutMapping("/{teamId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateFeed(
            @PathVariable final Long teamId,
            @RequestBody final TeamUpdateRequest request
    ) {
        teamCommandService.updateTeam(teamId, request);
    }

    @DeleteMapping("/{teamId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTeam(@PathVariable final Long teamId) {
        teamCommandService.deleteTeam(teamId);
    }
}
