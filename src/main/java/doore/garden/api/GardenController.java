package doore.garden.api;

import doore.garden.application.GardenQueryService;
import doore.garden.application.dto.response.DayGardenResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/garden")
public class GardenController {
    private final GardenQueryService gardenQueryService;

    @GetMapping("/{teamId}")
    public ResponseEntity<List<DayGardenResponse>> getGardens(@PathVariable final Long teamId) {
        final List<DayGardenResponse> gardenResponse = gardenQueryService.getGardens(teamId);
        return ResponseEntity.status(HttpStatus.OK).body(gardenResponse);
    }
}
