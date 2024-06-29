package doore.garden.application;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import doore.garden.application.dto.response.DayGardenResponse;
import doore.garden.domain.Garden;
import doore.garden.domain.GardenType;
import doore.garden.domain.repository.GardenRepository;
import doore.helper.IntegrationTest;
import java.time.LocalDate;
import java.util.List;
import net.bytebuddy.asm.Advice.Local;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class GardenQueryServiceTest extends IntegrationTest {

    @Autowired
    GardenQueryService gardenQueryService;

    @Autowired
    GardenRepository gardenRepository;

    @Test
    @DisplayName("[성공] 팀의 텃밭을 정상적으로 조회할 수 있다.")
    public void getGardens_팀의_텃밭을_정상적으로_조회할_수_있다_성공() throws Exception {
        //given
        final Long teamId = 1L;
        final Long otherTeamId = 2L;
        final Garden garden = Garden.builder()
                .contributedDate(LocalDate.now())
                .teamId(teamId)
                .memberId(1L)
                .contributionId(1L)
                .type(GardenType.DOCUMENT_UPLOAD)
                .build();
        final Garden before15WeeksGarden = Garden.builder()
                .contributedDate(LocalDate.now().minusWeeks(15))
                .teamId(teamId)
                .memberId(1L)
                .contributionId(2L)
                .type(GardenType.STUDY_CURRICULUM_COMPLETION)
                .build();
        final Garden otherTeamGarden = Garden.builder()
                .contributedDate(LocalDate.now())
                .teamId(otherTeamId)
                .memberId(1L)
                .contributionId(3L)
                .type(GardenType.STUDY_CURRICULUM_COMPLETION)
                .build();

        gardenRepository.saveAll(List.of(garden, before15WeeksGarden, otherTeamGarden));

        //when
        final List<Garden> allGardens = gardenRepository.findAll();
        final List<DayGardenResponse> gardenResponses = gardenQueryService.getGardens(teamId);

        //then
        assertEquals(3, allGardens.size());
        assertEquals(1, gardenResponses.size()); //최근 15주의 텃밭 데이터만 가져온다, 우리 팀의 텃밭 데이터만 가져온다.
    }

    @Test
    @DisplayName("[성공] 팀의 텃밭은 기여된 날짜를 기준으로 오름차 정렬된다.")
    public void getGardens_팀의_텃밭은_기여된_날짜를_기준으로_오름차_정렬된다_성공() throws Exception {
        //given
        final Long teamId = 1L;
        final LocalDate now = LocalDate.now();
        final LocalDate before1Week = now.minusWeeks(1);
        final LocalDate before2Week = now.minusWeeks(2);
        final Garden garden = Garden.builder()
                .contributedDate(now)
                .teamId(teamId)
                .memberId(1L)
                .contributionId(1L)
                .type(GardenType.DOCUMENT_UPLOAD)
                .build();
        final Garden before1WeekGarden = Garden.builder()
                .contributedDate(before1Week)
                .teamId(teamId)
                .memberId(1L)
                .contributionId(2L)
                .type(GardenType.STUDY_CURRICULUM_COMPLETION)
                .build();
        final Garden before2WeekGarden = Garden.builder()
                .contributedDate(before2Week)
                .teamId(teamId)
                .memberId(1L)
                .contributionId(3L)
                .type(GardenType.STUDY_CURRICULUM_COMPLETION)
                .build();

        gardenRepository.saveAll(List.of(before1WeekGarden, before2WeekGarden, garden));

        //when
        final List<Garden> allGardens = gardenRepository.findAll();
        final List<DayGardenResponse> gardenResponses = gardenQueryService.getGardens(teamId);

        //then
        assertAll(() -> {
            assertEquals(now, gardenResponses.get(0).contributeDate());
            assertEquals(before1Week, gardenResponses.get(1).contributeDate());
            assertEquals(before2WeekGarden, gardenResponses.get(2).contributeDate());
        });
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
