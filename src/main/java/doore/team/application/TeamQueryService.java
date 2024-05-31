package doore.team.application;

import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;
import static doore.member.exception.MemberExceptionType.UNAUTHORIZED;

import doore.member.domain.repository.MemberRepository;
import doore.member.exception.MemberException;
import doore.study.application.dto.response.StudyNameResponse;
import doore.study.domain.repository.StudyRepository;
import doore.team.application.dto.response.MyTeamsAndStudiesResponse;
import doore.team.application.dto.response.TeamReferenceResponse;
import doore.team.domain.Team;
import doore.team.domain.TeamRepository;
import java.util.ArrayList;
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

    private void validateMember(final Long memberId) {
        memberRepository.findById(memberId).orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
    }

    private void checkSameMemberIdAndTokenMemberId(final Long memberId, final Long tokenMemberId) {
        if (!memberId.equals(tokenMemberId)) {
            throw new MemberException(UNAUTHORIZED);
        }
    }
}
