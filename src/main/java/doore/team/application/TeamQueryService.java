package doore.team.application;

import doore.team.application.dto.response.TeamReferenceResponse;
import doore.team.domain.TeamRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TeamQueryService {
    private final TeamRepository teamRepository;

    public List<TeamReferenceResponse> findMyTeams(final Long memberId) {
        return teamRepository.findAllByMemberId(memberId)
                .stream()
                .map(TeamReferenceResponse::from)
                .toList();
    }
}
