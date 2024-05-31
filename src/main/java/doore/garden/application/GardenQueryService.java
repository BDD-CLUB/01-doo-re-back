package doore.garden.application;

import doore.garden.application.dto.response.DayGardenResponse;
import doore.garden.domain.Garden;
import doore.garden.domain.repository.GardenRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GardenQueryService {
    private final GardenRepository gardenRepository;

    public List<DayGardenResponse> getAllGarden(Long teamId) {
        List<Garden> gardens = gardenRepository.findAllOfThisYearByTeamIdOrderByContributedDateAsc(teamId);
        return calculateContributes(gardens);
    }

    private List<DayGardenResponse> calculateContributes(List<Garden> gardens) {
        return gardens.stream()
                .collect(Collectors.groupingBy(Garden::getContributedDate, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> DayGardenResponse.of(entry.getKey(), entry.getValue().intValue()))
                .toList();
    }
}
