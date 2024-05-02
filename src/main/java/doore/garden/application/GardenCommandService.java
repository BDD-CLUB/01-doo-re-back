package doore.garden.application;

import static doore.garden.domain.GardenType.*;
import static org.springframework.data.util.TypeUtils.type;

import doore.document.domain.Document;
import doore.garden.domain.Garden;
import doore.garden.domain.GardenType;
import doore.study.domain.CurriculumItem;
import doore.study.domain.ParticipantCurriculumItem;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GardenCommandService {

    public void createGarden(Document document) {
        Garden garden = GardenType.valueOf(document.getClass().getSimpleName()).getSupplier().get().create(document);
    }

    public void createGarden(ParticipantCurriculumItem participantCurriculumItem) {
        Garden garden = GardenType.valueOf(participantCurriculumItem.getClass().getSimpleName()).getSupplier().get().create(participantCurriculumItem);
    }

}
