package doore.study.application;

import static doore.study.exception.StudyExceptionType.NOT_FOUND_PARTICIPANT;
import static doore.study.exception.StudyExceptionType.NOT_FOUND_STUDY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import doore.helper.IntegrationTest;
import doore.member.MemberFixture;
import doore.member.domain.Member;
import doore.member.domain.Participant;
import doore.member.domain.repository.MemberRepository;
import doore.member.domain.repository.ParticipantRepository;
import doore.study.StudyFixture;
import doore.study.application.dto.request.CurriculumItemManageRequest;
import doore.study.domain.CurriculumItem;
import doore.study.domain.ParticipantCurriculumItem;
import doore.study.domain.Study;
import doore.study.domain.repository.CurriculumItemRepository;
import doore.study.domain.repository.ParticipantCurriculumItemRepository;
import doore.study.domain.repository.StudyRepository;
import doore.study.exception.StudyException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CurriculumItemCommandServiceTest extends IntegrationTest {

    @Autowired
    private CurriculumItemCommandService curriculumItemCommandService;
    @Autowired
    protected CurriculumItemRepository curriculumItemRepository;
    @Autowired
    protected StudyRepository studyRepository;
    @Autowired
    protected MemberRepository memberRepository;
    @Autowired
    protected ParticipantRepository participantRepository;
    @Autowired
    protected ParticipantCurriculumItemRepository participantCurriculumItemRepository;

    private Study study;
    private CurriculumItem curriculumItem1;
    private CurriculumItem curriculumItem2;
    private CurriculumItem curriculumItem3;
    private Long invalidCurriculumItemId;
    private Long invalidStudyId;
    private CurriculumItemManageRequest request;

    @BeforeEach
    void setUp() {
        study = StudyFixture.algorithmStudy();
        studyRepository.save(study);

        invalidCurriculumItemId = 5L;
        invalidStudyId = 5L;

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
    @DisplayName("[실패] 존재하지 않는 커리큘럼의 완료 상태를 변경할 수 없다.")
    public void checkCurriculum_존재하지_않는_커리큘럼의_완료_상태를_변경할_수_없다() throws Exception {
        assertThatThrownBy(() -> {
            curriculumItemCommandService.checkCurriculum(1L, invalidCurriculumItemId);
        }).isInstanceOf(StudyException.class).hasMessage(NOT_FOUND_PARTICIPANT.errorMessage());
    }

    @Test
    @DisplayName("[성공] 커리큘럼의 상태 변경이 가능하다.")
    public void checkCurriculum_커리큘럼의_상태_변경이_가능하다() throws Exception {
        Member member = MemberFixture.아마란스();
        memberRepository.save(member);

        Participant participant = Participant.builder()
                .member(member)
                .studyId(study.getId())
                .build();
        participantRepository.save(participant);

        curriculumItemCommandService.manageCurriculum(request, study.getId());
        CurriculumItem curriculumItem = curriculumItemRepository.findById(4L).orElseThrow();
        curriculumItemCommandService.checkCurriculum(curriculumItem.getId(), participant.getId());
        ParticipantCurriculumItem result = participantCurriculumItemRepository.findByCurriculumItemIdAndParticipantId(
                curriculumItem.getId(), participant.getId()).orElseThrow();

        assertThat(result.getIsChecked()).isEqualTo(true);

        curriculumItemCommandService.checkCurriculum(curriculumItem.getId(), participant.getId());

        assertThat(result.getIsChecked()).isEqualTo(false);
    }

    @Test
    @DisplayName("[성공] 아이디가 없으면 커리큘럼을 생성한다.")
    public void createCurriculum_아이디가_없으면_커리큘럼을_생성한다() throws Exception {
        curriculumItemCommandService.manageCurriculum(request, study.getId());
        CurriculumItem resultCurriculumItem = curriculumItemRepository.findById(4L).orElseThrow();

        assertThat(resultCurriculumItem.getId()).isEqualTo(4);
        assertThat(resultCurriculumItem.getName()).isEqualTo("Algorithm Study");
    }

    @Test
    @DisplayName("[실패] 존재하지 않는 스터디는 커리큘럼이 생성되지 않는다.")
    public void createCurriculum_존재하지_않는_스터디는_커리큘럼이_생성되지_않는다() throws Exception {
        assertThatThrownBy(() -> {
            curriculumItemCommandService.manageCurriculum(request, invalidStudyId);
        }).isInstanceOf(StudyException.class).hasMessage(NOT_FOUND_STUDY.errorMessage());
    }

    @Test
    @DisplayName("[성공] 아이디가 존재하고 아이템 순서가 다르다면 커리큘럼의 아이템 순서를 변경한다.")
    public void updateCurriculum_아이디가_존재하고_아이템_순서가_다르다면_커리큘럼의_아이템_순서를_변경한다() throws Exception {
        curriculumItemCommandService.manageCurriculum(request, study.getId());
        CurriculumItem resultCurriculumItem = curriculumItemRepository.findById(2L).orElseThrow();

        assertThat(resultCurriculumItem.getItemOrder()).isEqualTo(3);
    }

    @Test
    @DisplayName("[성공] 아이디가 존재하고 내용이 다르다면 커리큘럼의 내용을 변경한다.")
    public void updateCurriculum_아이디가_존재하고_내용이_다르다면_커리큘럼의_내용을_변경한다() throws Exception {
        curriculumItemCommandService.manageCurriculum(request, study.getId());
        CurriculumItem resultCurriculumItem = curriculumItemRepository.findById(1L).orElseThrow();

        assertThat(resultCurriculumItem.getName()).isEqualTo(request.curriculumItems().get(0).getName());
    }

    @Test
    @DisplayName("[성공] 커리큘럼을 삭제하면 정상적으로 삭제된다.")
    public void deleteCurriculum_커리큘럼을_삭제하면_정상적으로_삭제된다() throws Exception {
        curriculumItemCommandService.manageCurriculum(request, study.getId());

        assertThat(curriculumItemRepository.count()).isEqualTo(3);
    }

    @Test
    @DisplayName("[성공] 모든 과정이 끝나면 아이템 순서에 대해 연속적인 오름차순으로 정렬된다.")
    public void sortCurriculum_모든_과정이_끝나면_아이템_순서에_대해_연속적인_오름차순으로_정렬된다() throws Exception {
        curriculumItemCommandService.manageCurriculum(request, study.getId());

        List<CurriculumItem> curriculumItems = curriculumItemRepository.findAll();

        assertThat(curriculumItems.get(0).getItemOrder()).isEqualTo(1);
        assertThat(curriculumItems.get(0).getName()).isEqualTo("Change Spring Study");
        assertThat(curriculumItems.get(1).getItemOrder()).isEqualTo(3);
        assertThat(curriculumItems.get(1).getName()).isEqualTo("CS Study");
        assertThat(curriculumItems.get(2).getItemOrder()).isEqualTo(2);
        assertThat(curriculumItems.get(2).getName()).isEqualTo("Algorithm Study");
    }

}
