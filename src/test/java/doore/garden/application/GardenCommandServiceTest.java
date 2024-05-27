package doore.garden.application;

import static doore.garden.domain.GardenType.STUDY_CURRICULUM_COMPLETION;
import static doore.garden.domain.GardenType.DOCUMENT_UPLOAD;
import static doore.study.CurriculumItemFixture.curriculumItem;
import static org.junit.jupiter.api.Assertions.assertEquals;

import doore.document.DocumentFixture;
import doore.document.application.DocumentCommandService;
import doore.document.domain.Document;
import doore.garden.domain.Garden;
import doore.garden.domain.repository.GardenRepository;
import doore.helper.IntegrationTest;
import doore.study.application.CurriculumItemCommandService;
import doore.study.domain.CurriculumItem;
import doore.study.domain.ParticipantCurriculumItem;
import doore.study.domain.repository.CurriculumItemRepository;
import doore.study.domain.repository.ParticipantCurriculumItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class GardenCommandServiceTest extends IntegrationTest {

    @Autowired
    CurriculumItemCommandService curriculumItemCommandService;

    @Autowired
    DocumentCommandService documentCommandService;

    @Autowired
    ParticipantCurriculumItemRepository participantCurriculumItemRepository;
    @Autowired
    GardenRepository gardenRepository;
    @Autowired
    CurriculumItemRepository curriculumItemRepository;





}
