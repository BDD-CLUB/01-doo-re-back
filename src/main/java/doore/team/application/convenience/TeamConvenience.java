package doore.team.application.convenience;

import doore.team.domain.Team;
import doore.team.domain.repository.TeamRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamConvenience {

    private final TeamRepository teamRepository;

    public List<Team> findAllByMemberId(final Long memberId) {
        return teamRepository.findAllByMemberId(memberId);
    }
}
