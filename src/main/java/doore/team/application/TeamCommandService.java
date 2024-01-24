package doore.team.application;

import static doore.team.exception.TeamExceptionType.NOT_FOUND_TEAM;

import doore.team.application.dto.request.TeamCreateRequest;
import doore.team.application.dto.request.TeamUpdateRequest;
import doore.team.domain.Team;
import doore.team.domain.TeamRepository;
import doore.team.exception.TeamException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TeamCommandService {

    private final TeamRepository teamRepository;

    public void createTeam(final TeamCreateRequest request) {
        // TODO: 팀 생성자를 팀 관리자로 등록
        // TODO: 팀 이미지를 AWS S3에 저장
        final Team team = Team.builder()
                .name(request.name())
                .description(request.description())
                .build();
        teamRepository.save(team);
    }

    public void updateTeam(final Long teamId, final TeamUpdateRequest request) {
        final Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamException(NOT_FOUND_TEAM));
        team.update(request.name(), request.description());
    }

    public void deleteTeam(final Long teamId) {
        final Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamException(NOT_FOUND_TEAM));
        teamRepository.delete(team);
    }
}
