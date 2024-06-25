package doore.garden.application;

import doore.garden.application.dto.response.DayGardenResponse;
import doore.garden.domain.Garden;
import doore.garden.domain.repository.GardenRepository;
import doore.team.domain.Team;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GardenQueryService {
    private final GardenRepository gardenRepository;

    public List<DayGardenResponse> getAllGarden(final Long teamId) {
        final List<Garden> gardens = gardenRepository.findAllOfThisYearByTeamIdOrderByContributedDateAsc(teamId);
        return calculateContributes(gardens);
    }

    public DayGardenResponse getTodayGarden(final Long teamId) {
        final List<Garden> gardens = gardenRepository.findTodayGardenByTeamId(teamId);
        return calculateContributes(gardens).get(0);
    }

    public List<DayGardenResponse> getThisWeekGarden(final Long teamId) {
        final List<Garden> gardens = gardenRepository.findThisWeekGardenByTeamId(teamId);
        return calculateContributes(gardens);
    }

    private List<DayGardenResponse> calculateContributes(final List<Garden> gardens) {
        return gardens.stream()
                .collect(Collectors.groupingBy(Garden::getContributedDate, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> DayGardenResponse.of(entry.getKey(), entry.getValue().intValue()))
                .toList();
    }
}
