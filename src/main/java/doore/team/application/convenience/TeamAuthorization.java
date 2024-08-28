package doore.team.application.convenience;

import static doore.team.exception.TeamExceptionType.NOT_FOUND_TEAM;

import doore.team.domain.TeamRepository;
import doore.team.exception.TeamException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamAuthorization {
    private final TeamRepository teamRepository;

    public void validateExistTeam(Long groupId) {
        teamRepository.findById(groupId).orElseThrow(() -> new TeamException(NOT_FOUND_TEAM));
    }
}
