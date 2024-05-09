package doore.garden.application;

import doore.garden.application.dto.response.DayGardenResponse;
import doore.garden.domain.Garden;
import doore.garden.domain.repository.GardenRepository;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GardenQueryService {
    private final GardenRepository gardenRepository;
    List<DayGardenResponse> fullGardenResponse;

    public List<DayGardenResponse>  getFullGarden(Long teamId) {
        List<Garden> gardens = gardenRepository.findAllOfThisYearByTeamIdOrderByContributedDateAsc(teamId);
        calculateContributes(gardens);
        //todo: size 테스트 코드
        return fullGardenResponse;
    }

    private void calculateContributes(List<Garden> gardens) {
        LocalDate prevDate = gardens.get(0).getContributedDate();
        LocalDate curDate;
        int contributeNumber = 1;
        for (int i = 1; i < gardens.size(); i++) {
            curDate = gardens.get(i).getContributedDate();
            if (curDate.equals(prevDate)) {
                contributeNumber++;
                continue;
            }
            updateFullGardenResponse(prevDate, contributeNumber);
            contributeNumber = 1;
            prevDate = curDate;
        }
    }

    private void updateFullGardenResponse(LocalDate date, int contributeNumber) {
        int weekOfYear = getWeekOfYear(date);
        int dayOfWeek = date.getDayOfWeek().getValue() - 1;
        fullGardenResponse.add(DayGardenResponse.builder()
                .dayOfYear(date.getDayOfYear() - 1)
                .weekOfYear(weekOfYear)
                .dayOfWeek(dayOfWeek)
                .attributeNumber(contributeNumber)
                .build()
        );
    }

    private int getWeekOfYear(LocalDate date) {
        WeekFields weekFields = WeekFields.of(Locale.KOREA);
        return date.get(weekFields.weekOfWeekBasedYear());
    }
}
