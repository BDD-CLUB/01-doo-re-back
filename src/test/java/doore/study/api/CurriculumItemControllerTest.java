package doore.study.api;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import doore.helper.IntegrationTest;
import doore.member.domain.repository.ParticipantRepository;
import doore.study.StudyFixture;
import doore.study.application.dto.request.CurriculumItemManageRequest;
import doore.study.domain.CurriculumItem;
import doore.study.domain.Study;
import doore.study.domain.repository.CurriculumItemRepository;
import doore.study.domain.repository.StudyRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CurriculumItemControllerTest extends IntegrationTest {

    @Autowired
    protected StudyRepository studyRepository;
    @Autowired
    protected CurriculumItemRepository curriculumItemRepository;
    @Autowired
    protected ParticipantRepository participantRepository;

    private Study study;
    private CurriculumItem curriculumItem1;
    private CurriculumItem curriculumItem2;
    private CurriculumItem curriculumItem3;
    private CurriculumItemManageRequest request;

    @BeforeEach
    void setUp() {
        study = StudyFixture.algorithmStudy();
        studyRepository.save(study);

        curriculumItem1 = CurriculumItem.builder().id(1L).itemOrder(1).name("Spring Study").study(study).build();
        curriculumItemRepository.save(curriculumItem1);
        curriculumItem2 = CurriculumItem.builder().id(2L).itemOrder(2).name("CS Study").study(study).build();
        curriculumItemRepository.save(curriculumItem2);
        curriculumItem3 = CurriculumItem.builder().id(3L).itemOrder(3).name("Infra Study").study(study).build();
        curriculumItemRepository.save(curriculumItem3);

        request = CurriculumItemManageRequest.builder()
                .curriculumItems(getCurriculumItems())
                .deletedCurriculumItems(getDeletedCurriculumItems())
                .build();
    }

    private List<CurriculumItem> getCurriculumItems() {
        List<CurriculumItem> curriculumItems = new ArrayList<>();
        curriculumItems.add(CurriculumItem.builder().id(1L).itemOrder(1).name("Change Spring Study").build());
        curriculumItems.add(CurriculumItem.builder().id(2L).itemOrder(4).name("CS Study").build());
        curriculumItems.add(CurriculumItem.builder().id(3L).itemOrder(2).name("Infra Study").build());
        curriculumItems.add(CurriculumItem.builder().id(4L).itemOrder(3).name("Algorithm Study").build());
        return curriculumItems;
    }

    private List<CurriculumItem> getDeletedCurriculumItems() {
        List<CurriculumItem> deletedCurriculumItems = new ArrayList<>();
        deletedCurriculumItems.add(CurriculumItem.builder().id(3L).itemOrder(2).name("Infra Study").build());
        return deletedCurriculumItems;
    }

    @Test
    @DisplayName("[성공] 커리큘럼 관리가 정상적으로 이루어진다.")
    public void manageCurriculum_커리큘럼_관리가_정상적으로_이루어진다 () throws Exception {
        String url = "/studies/" + study.getId() + "/curriculums";
        callPostApi(url, request).andExpect(status().isCreated());
    }
}
