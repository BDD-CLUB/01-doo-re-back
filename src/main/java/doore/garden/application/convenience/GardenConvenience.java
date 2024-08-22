package doore.garden.application.convenience;

import doore.garden.domain.Garden;
import doore.garden.domain.GardenType;
import doore.garden.domain.repository.GardenRepository;
import doore.study.domain.ParticipantCurriculumItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class GardenConvenience {
    private final GardenRepository gardenRepository;

    public void createCurriculumGarden(final ParticipantCurriculumItem participantCurriculumItem) {
        final Garden garden = GardenType.getSupplierOf(participantCurriculumItem.getClass().getSimpleName())
                .of(participantCurriculumItem);
        gardenRepository.save(garden);
    }

    public void deleteCurriculumGarden(final ParticipantCurriculumItem participantCurriculumItem) {
        final Long contributionId = participantCurriculumItem.getId();
        final GardenType gardenType = GardenType.getGardenTypeOf(participantCurriculumItem.getClass().getSimpleName());
        gardenRepository.deleteByContributionIdAndType(contributionId, gardenType);
    }
}
