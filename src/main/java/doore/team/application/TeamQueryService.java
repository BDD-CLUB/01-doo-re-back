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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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

    public List<MyTeamsAndStudiesResponse> findMyTeamsAndStudies(Long memberId, Long tokenMemberId) {
        validateMember(memberId);
        checkSameMemberIdAndTokenMemberId(memberId, tokenMemberId);
        List<MyTeamsAndStudiesResponse> myTeamsAndStudiesResponses = new ArrayList<>();
        List<Team> myTeams = teamRepository.findAllByMemberId(memberId);

        for (Team myTeam : myTeams) {
            List<StudyNameResponse> studyNameResponses =
                    studyRepository.findAllByTeamId(myTeam.getId()).stream()
                            .map(StudyNameResponse::from)
                            .toList();
            MyTeamsAndStudiesResponse myTeamsAndStudiesResponse =
                    new MyTeamsAndStudiesResponse(myTeam.getId(), myTeam.getName(), studyNameResponses);
            myTeamsAndStudiesResponses.add(myTeamsAndStudiesResponse);
        }
        return myTeamsAndStudiesResponses;
    }

    public TeamResponse findTeamByTeamId(final Long teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new TeamException(NOT_FOUND_TEAM));
        List<MemberTeam> memberTeams = memberTeamRepository.findAllByTeamId(teamId);
        List<Long> memberIds = memberTeams.stream()
                .map(MemberTeam::getMember)
                .map(Member::getId)
                .toList();

        List<Attendance> attendances = attendanceRepository.findAllByMemberIdIn(memberIds);

        long countMemberTeam = memberIds.size();
        long countAttendanceMemberTeam = attendances.size();
        long attendanceRatio = countMemberTeam > 0 ? (long) ((countAttendanceMemberTeam * 100.0) / countMemberTeam) : 0;

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
        Map<Integer, TeamReferenceResponse> teamRanks = calculateTeamRanks();
        return teamRanks.entrySet().stream()
                .map(entry -> new TeamRankResponse(entry.getKey(), entry.getValue()))
                .toList();
    }

    private Map<Integer, TeamReferenceResponse> calculateTeamRanks() {
        List<Team> teams = teamRepository.findAll();
        Map<Integer, TeamReferenceResponse> teamRanks = new TreeMap<>(Collections.reverseOrder());
        for (Team team : teams) {
            List<DayGardenResponse> gardenResponses = gardenQueryService.getThisWeekGarden(team.getId());
            Integer point = gardenResponses.stream()
                    .mapToInt(DayGardenResponse::contributeCount)
                    .sum();
            teamRanks.put(point, TeamReferenceResponse.from(team));
        }
        return teamRanks;
    }
}
