package doore.garden.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import doore.garden.application.dto.response.DayGardenResponse;
import doore.garden.domain.Garden;
import doore.garden.domain.GardenType;
import doore.garden.domain.repository.GardenRepository;
import doore.helper.IntegrationTest;
import java.time.LocalDate;
import java.util.List;
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
    public void getAllGarden_팀의_텃밭을_정상적으로_조회할_수_있다_성공() throws Exception {
        //given
        Long teamId = 1L;
        Long otherTeamId = 2L;
        Garden garden = Garden.builder()
                .contributedDate(LocalDate.now())
                .teamId(teamId)
                .memberId(1L)
                .contributionId(1L)
                .type(GardenType.DOCUMENT_UPLOAD)
                .build();
        Garden lastYearGarden = Garden.builder()
                .contributedDate(LocalDate.now().minusYears(1))
                .teamId(teamId)
                .memberId(1L)
                .contributionId(2L)
                .type(GardenType.STUDY_CURRICULUM_COMPLETION)
                .build();
        Garden otherTeamGarden = Garden.builder()
                .contributedDate(LocalDate.now())
                .teamId(otherTeamId)
                .memberId(1L)
                .contributionId(3L)
                .type(GardenType.STUDY_CURRICULUM_COMPLETION)
                .build();

        gardenRepository.saveAll(List.of(garden, lastYearGarden, otherTeamGarden));

        //when
        List<Garden> allGardens = gardenRepository.findAll();
        assertEquals(1, gardenRepository.findAllOfThisYearByTeamIdOrderByContributedDateAsc(teamId).size());
        List<DayGardenResponse> gardenResponses = gardenQueryService.getAllGarden(teamId);

        //then
        assertEquals(3, allGardens.size());
        assertEquals(1, gardenResponses.size()); //올해의 텃밭 데이터만 가져온다, 우리 팀의 텃밭 데이터만 가져온다.
    }

   @Test
   @DisplayName("[성공] 정상적으로 동일한 날의 기여도를 계산할 수 있다.")
   public void calculateContributes_정상적으로_동일한_날의_기여도를_계산할_수_있다_성공() throws Exception {
     //given
       LocalDate January1st = LocalDate.of(LocalDate.now().getYear(), 1,1);
       Long teamId = 1L;
       Garden todaysGarden = Garden.builder()
               .contributedDate(January1st)
               .teamId(teamId)
               .memberId(1L)
               .contributionId(1L)
               .type(GardenType.DOCUMENT_UPLOAD)
               .build();
       Garden otherTodaysGarden = Garden.builder()
               .contributedDate(January1st)
               .teamId(teamId)
               .memberId(1L)
               .contributionId(2L)
               .type(GardenType.STUDY_CURRICULUM_COMPLETION)
               .build();
     List<Garden> gardens = List.of(todaysGarden,otherTodaysGarden);
     gardenRepository.saveAll(gardens);

     //when
     List<DayGardenResponse> gardenResponses = gardenQueryService.getAllGarden(teamId);

     //then
     assertEquals(1,gardenResponses.size());
     assertEquals(2,gardenResponses.get(0).contributeCount());
     assertEquals(0,gardenResponses.get(0).dayOfYear());
     assertEquals(0,gardenResponses.get(0).weekOfYear());
   }
}
