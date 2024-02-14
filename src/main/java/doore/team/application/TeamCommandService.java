package doore.team.application;

import static doore.team.exception.TeamExceptionType.EXPIRED_LINK;
import static doore.team.exception.TeamExceptionType.NOT_FOUND_TEAM;
import static doore.team.exception.TeamExceptionType.NOT_MATCH_LINK;

import doore.file.application.S3ImageFileService;
import doore.team.application.dto.request.TeamCreateRequest;
import doore.team.application.dto.request.TeamInviteLinkRequest;
import doore.team.application.dto.request.TeamUpdateRequest;
import doore.team.application.dto.response.TeamInviteLinkResponse;
import doore.team.domain.Team;
import doore.team.domain.TeamRepository;
import doore.team.exception.TeamException;
import doore.util.RandomUtil;
import doore.util.RedisUtil;
import java.util.Optional;
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
    private final RedisUtil redisUtil;

    private static final String INVITE_LINK_PREFIX = "teamId=%d";

    public void createTeam(final TeamCreateRequest request, final MultipartFile file) {
        // TODO: 팀 생성자를 팀 관리자로 등록
        final String imageUrl = s3ImageFileService.upload(file);
        try {
            final Team team = Team.builder()
                    .name(request.name())
                    .description(request.description())
                    .imageUrl(imageUrl)
                    .build();
            teamRepository.save(team);
        } catch (Exception e) {
            s3ImageFileService.deleteFile(imageUrl);
        }
    }

    public void updateTeam(final Long teamId, final TeamUpdateRequest request) {
        final Team team = validateExistTeam(teamId);
        team.update(request.name(), request.description());
    }

    public void updateTeamImage(final Long teamId, final MultipartFile file) {
        final Team team = validateExistTeam(teamId);

        if (team.hasImage()) {
            final String beforeImageUrl = team.getImageUrl();
            s3ImageFileService.deleteFile(beforeImageUrl);
        }
        final String newImageUrl = s3ImageFileService.upload(file);
        team.updateImageUrl(newImageUrl);
    }

    public void deleteTeam(final Long teamId) {
        final Team team = validateExistTeam(teamId);
        teamRepository.delete(team);
        if (team.hasImage()) {
            s3ImageFileService.deleteFile(team.getImageUrl());
        }
        // TODO: 2/2/24 팀이 삭제될 시 연관된 스터디와, 커리큘럼도 삭제
    }

    private Team validateExistTeam(final Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamException(NOT_FOUND_TEAM));
    }

    public TeamInviteLinkResponse generateTeamInviteLink(final Long teamId) {
        validateExistTeam(teamId);

        final Optional<String> link = redisUtil.getData(INVITE_LINK_PREFIX.formatted(teamId), String.class);
        if (link.isEmpty()) {
            final String randomCode = RandomUtil.generateRandomCode('0', 'z', 10);
            redisUtil.setDataExpire(INVITE_LINK_PREFIX.formatted(teamId), randomCode, RedisUtil.toTomorrow());
            return new TeamInviteLinkResponse(randomCode);
        }
        return new TeamInviteLinkResponse(link.get());
    }

    public void joinTeam(final Long teamId, final TeamInviteLinkRequest request) {
        validateExistTeam(teamId);

        Optional<String> link = redisUtil.getData(INVITE_LINK_PREFIX.formatted(teamId), String.class);
        if (link.isPresent()) {
            validateMatchLink(link.get(), request.link());
            // TODO: 2/14/24 권한 관련 작업이 추가되면 팀원으로 회원 추가, 이미 가입된 팀원이라면 예외 처리.
        }
        throw new TeamException(EXPIRED_LINK);
    }

    private void validateMatchLink(final String link, final String userLink) {
        if (!link.equals(userLink)) {
            throw new TeamException(NOT_MATCH_LINK);
        }
    }
}
