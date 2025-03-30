package doore.team.application;

import static doore.team.exception.TeamExceptionType.NOT_FOUND_TEAM;

import doore.garden.application.GardenQueryService;
import doore.garden.application.dto.response.DayGardenResponse;
import doore.member.application.convenience.MemberConvenience;
import doore.member.application.convenience.MemberValidateAccessPermission;
import doore.member.domain.repository.MemberTeamRepository;
import doore.member.domain.repository.TeamRoleRepository;
import doore.study.application.dto.response.StudyNameResponse;
import doore.study.domain.repository.StudyRepository;
import doore.team.application.dto.response.MyTeamsAndStudiesResponse;
import doore.team.application.dto.response.TeamRankResponse;
import doore.team.application.dto.response.TeamReferenceResponse;
import doore.team.application.dto.response.TeamResponse;
import doore.team.domain.Team;
import doore.team.domain.repository.TeamRepository;
import doore.team.exception.TeamException;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TeamQueryService {
    private final TeamRepository teamRepository;
    private final StudyRepository studyRepository;
    private final MemberTeamRepository memberTeamRepository;
    private final TeamRoleRepository teamRoleRepository;
    private final GardenQueryService gardenQueryService;

    private final MemberConvenience memberConvenience;
    private final MemberValidateAccessPermission memberValidateAccessPermission;

    public List<TeamReferenceResponse> getMyTeams(final Long memberId, final Long tokenMemberId) {
        memberValidateAccessPermission.validateExistMember(memberId);
        memberConvenience.checkSameMemberIdAndTokenMemberId(memberId, tokenMemberId);
        return teamRepository.findAllByMemberId(memberId)
                .stream()
                .map(TeamReferenceResponse::from)
                .toList();
    }

    public List<MyTeamsAndStudiesResponse> getMyTeamsAndStudies(final Long memberId) {
        final List<Team> myTeams = teamRepository.findAllByMemberId(memberId);

        return myTeams.stream()
                .map(team -> {
                    List<StudyNameResponse> studyNameResponses = studyRepository.findAllByTeamIdAndMemberId(
                                    team.getId(), memberId).stream()
                            .map(StudyNameResponse::from)
                            .toList();
                    return MyTeamsAndStudiesResponse.of(team, studyNameResponses);
                })
                .toList();
    }

    public TeamResponse getTeams(final Long teamId) {
        final Team team = teamRepository.findById(teamId).orElseThrow(() -> new TeamException(NOT_FOUND_TEAM));
        final Long teamLeaderId = teamRoleRepository.findLeaderIdByTeamId(teamId);

        return TeamResponse.of(team, teamLeaderId);
    }

    public List<TeamRankResponse> getTeamRanks() {
        final List<Team> teams = teamRepository.findAll();
        List<TeamRankResponse> teamRanks = teams.stream().map(this::convertTeamToTeamRankResponse).toList();
        return teamRanks.stream()
                .sorted(Comparator.comparingInt(TeamRankResponse::point).reversed())
                .toList();
    }

    private TeamRankResponse convertTeamToTeamRankResponse(final Team team) {
        final int point = calculatePoint(team);
        final List<DayGardenResponse> yearGardenResponses = gardenQueryService.getGardens(team.getId());
        return new TeamRankResponse(point, TeamReferenceResponse.from(team), yearGardenResponses);
    }

    private int calculatePoint(final Team team) {
        final List<DayGardenResponse> weekGardenResponses = gardenQueryService.getThisWeekGarden(team.getId());
        return weekGardenResponses.stream()
                .mapToInt(DayGardenResponse::contributeCount)
                .sum();
    }
}
