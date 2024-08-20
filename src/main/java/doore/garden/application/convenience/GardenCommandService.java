package doore.garden.application.convenience;

import doore.document.domain.Document;
import doore.garden.domain.Garden;
import doore.garden.domain.GardenType;
import doore.garden.domain.repository.GardenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GardenCommandService {
    private final GardenRepository gardenRepository;

    public void createDocumentGarden(final Document document) {
        final Garden garden = GardenType.getSupplierOf(document.getClass().getSimpleName()).of(document);
        gardenRepository.save(garden);
    }

    public void deleteDocumentGarden(final Document document) {
        final Long contributionId = document.getId();
        final GardenType gardenType = GardenType.getGardenTypeOf(document.getClass().getSimpleName());
        gardenRepository.deleteByContributionIdAndType(contributionId, gardenType);
    }
}
