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
    GardenCommandService gardenCommandService;

    @Autowired
    ParticipantCurriculumItemRepository participantCurriculumItemRepository;
    @Autowired
    GardenRepository gardenRepository;
    @Autowired
    CurriculumItemRepository curriculumItemRepository;


    @Test
    @DisplayName("[성공] 커리큘럼 체크시 정상적으로 텃밭을 생성할 수 있다.")
    public void createGarden_커리큘럼_체크시_정상적으로_텃밭에_반영된다_성공() throws Exception {
        //given
        CurriculumItem curriculumItem = curriculumItemRepository.save(curriculumItem());
        ParticipantCurriculumItem participantCurriculumItem = ParticipantCurriculumItem.builder()
                .participantId(1L)
                .curriculumItem(curriculumItem)
                .build();
        participantCurriculumItemRepository.save(participantCurriculumItem);

        //when
        gardenCommandService.createGarden(participantCurriculumItem);

        //then
        Garden garden = gardenRepository.findAll().get(0);
        assertEquals(garden.getContributionId(), participantCurriculumItem.getId());
        assertEquals(garden.getType(), STUDY_CURRICULUM_COMPLETION);
    }

    @Test
    @DisplayName("[성공] 학습자료 업로드시 정상적으로 텃밭을 생성할 수 있다.")
    public void createGarden_학습자료_업로드시_정상적으로_텃밭에_반영된다_성공() throws Exception {
        //given
        Document document = new DocumentFixture().buildDocument();

        //when
        gardenCommandService.createGarden(document);

        //then
        Garden garden = gardenRepository.findAll().get(0);
        assertEquals(garden.getContributionId(), document.getId());
        assertEquals(garden.getType(), DOCUMENT_UPLOAD);
    }

    @Test
    @DisplayName("[성공] 커리큘럼 체크 해재시 정상적으로 텃밭에 반영된다.")
    public void deleteGarden_커리큘럼_체크_해재시_정상적으로_텃밭에_반영된다_성공() throws Exception {
        //given
        CurriculumItem curriculumItem = curriculumItemRepository.save(curriculumItem());
        ParticipantCurriculumItem participantCurriculumItem = ParticipantCurriculumItem.builder()
                .participantId(1L)
                .curriculumItem(curriculumItem)
                .build();
        participantCurriculumItemRepository.save(participantCurriculumItem);

        gardenCommandService.createGarden(participantCurriculumItem);
        assertEquals(1, gardenRepository.findAll().size());

        //when
        gardenCommandService.deleteGarden(participantCurriculumItem);

        //then
        assertEquals(0, gardenRepository.findAll().size());
    }
}
