package doore.garden.application;

import doore.document.domain.Document;
import doore.garden.domain.Garden;
import doore.garden.domain.GardenType;
import doore.garden.domain.repository.GardenRepository;
import doore.study.domain.ParticipantCurriculumItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GardenCommandService {
    private final GardenRepository gardenRepository;
    public void createGarden(Document document) {
        Garden garden = GardenType.getSupplierOf(document.getClass().getSimpleName()).create(document);
        gardenRepository.save(garden);
    }

    public void createGarden(ParticipantCurriculumItem participantCurriculumItem) {
        Garden garden = GardenType.getSupplierOf(participantCurriculumItem.getClass().getSimpleName()).create(participantCurriculumItem);
        gardenRepository.save(garden);
    }

    public void deleteGarden(Long contributionId, GardenType gardenType) {
        gardenRepository.deleteByContributionIdAndType(contributionId, gardenType);
    }

}
