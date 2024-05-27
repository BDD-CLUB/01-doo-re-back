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
        Garden garden = GardenType.getSupplierOf(document.getClass().getSimpleName()).of(document);
        gardenRepository.save(garden);
    }

    public void deleteGarden(Document document) {
        Long contributionId = document.getId();
        GardenType gardenType = GardenType.getGardenTypeOf(document.getClass().getSimpleName());
        gardenRepository.deleteByContributionIdAndType(contributionId, gardenType);
    }

    public void createGarden(ParticipantCurriculumItem participantCurriculumItem) {
        Garden garden = GardenType.getSupplierOf(participantCurriculumItem.getClass().getSimpleName())
                .of(participantCurriculumItem);
        gardenRepository.save(garden);
    }

    public void deleteGarden(ParticipantCurriculumItem participantCurriculumItem) {
        Long contributionId = participantCurriculumItem.getId();
        GardenType gardenType = GardenType.getGardenTypeOf(participantCurriculumItem.getClass().getSimpleName());
        gardenRepository.deleteByContributionIdAndType(contributionId, gardenType);
    }
}
