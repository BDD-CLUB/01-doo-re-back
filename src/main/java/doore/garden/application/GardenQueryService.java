package doore.garden.application;

import doore.garden.application.dto.response.DayGardenResponse;
import doore.garden.domain.Garden;
import doore.garden.domain.repository.GardenRepository;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GardenQueryService {
    private final GardenRepository gardenRepository;

    public List<DayGardenResponse>  getFullGarden(Long teamId) {
        List<Garden> gardens = gardenRepository.findAllOfThisYearByTeamIdOrderByContributedDateAsc(teamId);
        return calculateContributes(gardens);
    }

    private List<DayGardenResponse> calculateContributes(List<Garden> gardens) {
        List<DayGardenResponse> fullGardenResponse = new ArrayList<>();
        LocalDate prevDate = gardens.get(0).getContributedDate();
        LocalDate curDate;
        int contributeNumber = 1;
        for (int i = 1; i < gardens.size(); i++) {
            curDate = gardens.get(i).getContributedDate();
            if (curDate.equals(prevDate)) {
                contributeNumber++;
                continue;
            }
            fullGardenResponse.add(DayGardenResponse.of(prevDate, contributeNumber));
            contributeNumber = 1;
            prevDate = curDate;
        }
        fullGardenResponse.add(DayGardenResponse.of(prevDate, contributeNumber));
        return fullGardenResponse;
    }
}
