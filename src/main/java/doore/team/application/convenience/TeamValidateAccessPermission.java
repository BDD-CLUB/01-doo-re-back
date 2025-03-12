package doore.team.application.convenience;

import static doore.team.exception.TeamExceptionType.NOT_FOUND_TEAM;

import doore.team.domain.Team;
import doore.team.domain.repository.TeamRepository;
import doore.team.exception.TeamException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamValidateAccessPermission {
    private final TeamRepository teamRepository;

    public void validateExistTeam(final Long teamId) {
        teamRepository.findById(teamId).orElseThrow(() -> new TeamException(NOT_FOUND_TEAM));
    }

    public Team getValidateExistTeam(final Long teamId) {
        return teamRepository.findById(teamId).orElseThrow(() -> new TeamException(NOT_FOUND_TEAM));
    }
}
