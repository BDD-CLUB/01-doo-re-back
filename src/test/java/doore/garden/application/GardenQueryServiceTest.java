package doore.garden.application;

import static doore.member.MemberFixture.미나;
import static doore.team.TeamFixture.team;
import static doore.team.exception.TeamExceptionType.NOT_FOUND_TEAM;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import doore.garden.application.dto.response.DayGardenResponse;
import doore.garden.domain.Garden;
import doore.garden.domain.GardenType;
import doore.garden.domain.repository.GardenRepository;
import doore.helper.IntegrationTest;
import doore.member.domain.Member;
import doore.member.domain.repository.MemberRepository;
import doore.team.domain.Team;
import doore.team.domain.repository.TeamRepository;
import doore.team.exception.TeamException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class GardenQueryServiceTest extends IntegrationTest {

    @Autowired
    GardenQueryService gardenQueryService;

    @Autowired
    GardenRepository gardenRepository;
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    MemberRepository memberRepository;

    private Member member;
    private Team team;
    private Team otherTeam;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(미나());
        team = teamRepository.save(team());
        otherTeam = teamRepository.save(team());
    }

    @Test
    @DisplayName("[성공] 팀의 텃밭을 정상적으로 조회할 수 있다.")
    public void getGardens_팀의_텃밭을_정상적으로_조회할_수_있다_성공() throws Exception {
        //given
        final Garden garden = Garden.builder()
                .contributedDate(LocalDate.now())
                .teamId(team.getId())
                .memberId(member.getId())
                .contributionId(1L)
                .type(GardenType.DOCUMENT_UPLOAD)
                .build();
        final Garden before15WeeksGarden = Garden.builder()
                .contributedDate(LocalDate.now().minusWeeks(15))
                .teamId(otherTeam.getId())
                .memberId(member.getId())
                .contributionId(2L)
                .type(GardenType.STUDY_CURRICULUM_COMPLETION)
                .build();
        final Garden otherTeamGarden = Garden.builder()
                .contributedDate(LocalDate.now())
                .teamId(otherTeam.getId())
                .memberId(member.getId())
                .contributionId(3L)
                .type(GardenType.STUDY_CURRICULUM_COMPLETION)
                .build();

        gardenRepository.saveAll(List.of(garden, before15WeeksGarden, otherTeamGarden));

        //when
        final List<Garden> allGardens = gardenRepository.findAll();
        final List<DayGardenResponse> gardenResponses = gardenQueryService.getGardens(team.getId());

        //then
        assertEquals(3, allGardens.size());
        assertEquals(1, gardenResponses.size()); //최근 15주의 텃밭 데이터만 가져온다, 우리 팀의 텃밭 데이터만 가져온다.
    }

    @Test
    @DisplayName("[실패] 존재하지 않는 팀의 텃밭을 조회하면 실패한다.")
    public void getGardens_존재하지_않는_팀의_텃밭을_조회하면_실패한다_실패() {
        final Long invalidTeamId = 10L;

        assertThatThrownBy(() -> {
            gardenQueryService.getGardens(invalidTeamId);
        }).isInstanceOf(TeamException.class).hasMessage(NOT_FOUND_TEAM.errorMessage());
    }

    @Test
    @DisplayName("[성공] 정상적으로 동일한 날의 기여도를 계산할 수 있다.")
    public void calculateContributes_정상적으로_동일한_날의_기여도를_계산할_수_있다_성공() throws Exception {
        //given
        final LocalDate before1Week = LocalDate.now().minusWeeks(1);
        final Long teamId = 1L;
        final Garden todaysGarden = Garden.builder()
                .contributedDate(before1Week)
                .teamId(teamId)
                .memberId(1L)
                .contributionId(1L)
                .type(GardenType.DOCUMENT_UPLOAD)
                .build();
        final Garden otherTodaysGarden = Garden.builder()
                .contributedDate(before1Week)
                .teamId(teamId)
                .memberId(1L)
                .contributionId(2L)
                .type(GardenType.STUDY_CURRICULUM_COMPLETION)
                .build();
        final List<Garden> gardens = List.of(todaysGarden, otherTodaysGarden);
        gardenRepository.saveAll(gardens);

        //when
        final List<DayGardenResponse> gardenResponses = gardenQueryService.getGardens(teamId);

        //then
        assertEquals(1, gardenResponses.size());
        assertEquals(2, gardenResponses.get(0).contributeCount());
        assertEquals(before1Week, gardenResponses.get(0).contributeDate());
    }
}
