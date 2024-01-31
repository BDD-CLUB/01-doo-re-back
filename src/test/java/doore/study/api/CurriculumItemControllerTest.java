package doore.study.api;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.assertj.core.api.Assertions.assertThat;

import doore.helper.IntegrationTest;
import doore.member.domain.repository.ParticipantRepository;
import doore.study.CurriculumItemFixture;
import doore.study.StudyFixture;
import doore.study.application.dto.request.CurriculumItemRequest;
import doore.study.domain.CurriculumItem;
import doore.study.domain.repository.CurriculumItemRepository;
import doore.study.domain.Study;
import doore.study.domain.repository.StudyRepository;
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

    @BeforeEach
    void setUp() {
        study = StudyFixture.algorithmStudy();
        studyRepository.save(study);
    }

    @Test
    @DisplayName("커리큘림이 정상 등록된다. [성공]")
    public void 커리큘림이_정상_등록된다_성공() throws Exception {
        CurriculumItemRequest request = CurriculumItemRequest.builder().name("Spring MVC").build();
        String url = "/studies/" + study.getId() + "/curriculums";
        callPostApi(url, request).andExpect(status().isCreated());
    }

    @Test
    @DisplayName("커리큘럼이 정상 삭제된다. [성공]")
    public void 커리큘럼이_정상_삭제된다_성공() throws Exception {
        CurriculumItem curriculumItem = CurriculumItemFixture.curriculumItem();
        curriculumItemRepository.save(curriculumItem);
        String url = "/studies/" + study.getId() + "/curriculums/" + curriculumItem.getId();
        callDeleteApi(url).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("필수 정보를 입력하지 않았다면 커리큘럼 등록은 실패한다. [실패]")
    public void 필수_정보를_입력하지_않았다면_커리큘럼_등록과_수정은_실패한다_실패() throws Exception {
        CurriculumItemRequest request = CurriculumItemRequest.builder().name(null).build();
        String url = "/studies/" + study.getId() + "/curriculums";
        callPostApi(url, request).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("커리큘럼이 정상 수정된다. [성공]")
    public void 커리큘럼이_정상_수정된다_성공() throws Exception {
        CurriculumItem curriculumItem = CurriculumItemFixture.curriculumItem();
        curriculumItemRepository.save(curriculumItem);
        CurriculumItemRequest request = CurriculumItemRequest.builder().name("Change Spring MVC").build();
        String url = "/studies/" + study.getId() + "/curriculums/" + curriculumItem.getId();
        callPatchApi(url, request).andExpect(status().isNoContent());
    }

}
