package doore.team.application;

import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;
import static doore.member.exception.MemberExceptionType.UNAUTHORIZED;
import static doore.team.exception.TeamExceptionType.NOT_FOUND_TEAM;

import doore.attendance.domain.Attendance;
import doore.attendance.domain.repository.AttendanceRepository;
import doore.garden.application.GardenQueryService;
import doore.garden.application.dto.response.DayGardenResponse;
import doore.member.domain.Member;
import doore.member.domain.MemberTeam;
import doore.member.domain.repository.MemberRepository;
import doore.member.domain.repository.MemberTeamRepository;
import doore.member.exception.MemberException;
import doore.study.application.dto.response.StudyNameResponse;
import doore.study.domain.repository.StudyRepository;
import doore.team.application.dto.response.MyTeamsAndStudiesResponse;
import doore.team.application.dto.response.TeamRankResponse;
import doore.team.application.dto.response.TeamReferenceResponse;
import doore.team.application.dto.response.TeamResponse;
import doore.team.domain.Team;
import doore.team.domain.TeamRepository;
import doore.team.exception.TeamException;
import java.util.ArrayList;
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
    private final MemberRepository memberRepository;
    private final StudyRepository studyRepository;
    private final AttendanceRepository attendanceRepository;
    private final MemberTeamRepository memberTeamRepository;
    private final GardenQueryService gardenQueryService;

    public List<TeamReferenceResponse> findMyTeams(final Long memberId, final Long tokenMemberId) {
        validateMember(memberId);
        checkSameMemberIdAndTokenMemberId(memberId, tokenMemberId);
        return teamRepository.findAllByMemberId(memberId)
                .stream()
                .map(TeamReferenceResponse::from)
                .toList();
    }

    public List<MyTeamsAndStudiesResponse> findMyTeamsAndStudies(final Long memberId, final Long tokenMemberId) {
        validateMember(memberId);
        checkSameMemberIdAndTokenMemberId(memberId, tokenMemberId);
        final List<MyTeamsAndStudiesResponse> myTeamsAndStudiesResponses = new ArrayList<>();
        final List<Team> myTeams = teamRepository.findAllByMemberId(memberId);

        for (final Team myTeam : myTeams) {
            final List<StudyNameResponse> studyNameResponses =
                    studyRepository.findAllByTeamId(myTeam.getId()).stream()
                            .map(StudyNameResponse::from)
                            .toList();
            final MyTeamsAndStudiesResponse myTeamsAndStudiesResponse =
                    new MyTeamsAndStudiesResponse(myTeam.getId(), myTeam.getName(), studyNameResponses);
            myTeamsAndStudiesResponses.add(myTeamsAndStudiesResponse);
        }
        return myTeamsAndStudiesResponses;
    }

    public TeamResponse findTeamByTeamId(final Long teamId) {
        final Team team = teamRepository.findById(teamId).orElseThrow(() -> new TeamException(NOT_FOUND_TEAM));
        final List<MemberTeam> memberTeams = memberTeamRepository.findAllByTeamId(teamId);
        final List<Long> memberIds = memberTeams.stream()
                .map(MemberTeam::getMember)
                .map(Member::getId)
                .toList();

        final List<Attendance> attendances = attendanceRepository.findAllByMemberIdIn(memberIds);

        final long countMemberTeam = memberIds.size();
        final long countAttendanceMemberTeam = attendances.size();
        final long attendanceRatio =
                countMemberTeam > 0 ? (long) ((countAttendanceMemberTeam * 100.0) / countMemberTeam) : 0;

        return TeamResponse.of(team, attendanceRatio);
    }

    private void validateMember(final Long memberId) {
        memberRepository.findById(memberId).orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
    }

    private void checkSameMemberIdAndTokenMemberId(final Long memberId, final Long tokenMemberId) {
        if (!memberId.equals(tokenMemberId)) {
            throw new MemberException(UNAUTHORIZED);
        }
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
        final List<DayGardenResponse> yearGardenResponses = gardenQueryService.getAllGarden(team.getId());
        return new TeamRankResponse(point, TeamReferenceResponse.from(team), yearGardenResponses);
    }

    private int calculatePoint(final Team team) {
        final List<DayGardenResponse> weekGardenResponses = gardenQueryService.getThisWeekGarden(team.getId());
        return weekGardenResponses.stream()
                .mapToInt(DayGardenResponse::contributeCount)
                .sum();
    }
}
