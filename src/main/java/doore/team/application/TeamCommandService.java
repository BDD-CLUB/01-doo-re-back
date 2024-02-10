package doore.team.application;

import static doore.team.exception.TeamExceptionType.NOT_FOUND_TEAM;

import doore.file.application.S3ImageFileService;
import doore.team.application.dto.request.TeamCreateRequest;
import doore.team.application.dto.request.TeamUpdateRequest;
import doore.team.domain.Team;
import doore.team.domain.TeamRepository;
import doore.team.exception.TeamException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class TeamCommandService {

    private final TeamRepository teamRepository;
    private final S3ImageFileService s3ImageFileService;

    public void createTeam(final TeamCreateRequest request, final MultipartFile file) {
        // TODO: 팀 생성자를 팀 관리자로 등록
        final String imageUrl = s3ImageFileService.upload(file);
        final Team team = Team.builder()
                .name(request.name())
                .description(request.description())
                .imageUrl(imageUrl)
                .build();
        teamRepository.save(team);
    }

    public void updateTeam(final Long teamId, final TeamUpdateRequest request) {
        final Team team = findTeamById(teamId);
        team.update(request.name(), request.description());
    }

    public void updateTeamImage(final Long teamId, final MultipartFile file) {
        final Team team = findTeamById(teamId);

        if (team.hasImage()) {
            final String beforeImageUrl = team.getImageUrl();
            s3ImageFileService.deleteFile(beforeImageUrl);
        }
        final String newImageUrl = s3ImageFileService.upload(file);
        team.updateImageUrl(newImageUrl);
    }

    public void deleteTeam(final Long teamId) {
        final Team team = findTeamById(teamId);
        teamRepository.delete(team);
        // TODO: 2/2/24 팀이 삭제될 시 연관된 스터디와, 커리큘럼도 삭제
    }

    private Team findTeamById(final Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamException(NOT_FOUND_TEAM));
    }
}
