package doore.study.application;

import static doore.crop.CropFixture.rice;
import static doore.member.MemberFixture.미나;
import static doore.member.domain.StudyRoleType.ROLE_스터디장;
import static doore.member.exception.MemberExceptionType.UNAUTHORIZED;
import static doore.study.CurriculumItemFixture.curriculumItem;
import static doore.study.ParticipantCurriculumItemFixture.participantCurriculumItem;
import static doore.study.StudyFixture.algorithmStudy;
import static doore.study.exception.StudyExceptionType.NOT_FOUND_STUDY;
import static doore.team.TeamFixture.team;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import doore.crop.domain.Crop;
import doore.crop.domain.repository.CropRepository;
import doore.helper.IntegrationTest;
import doore.member.domain.Member;
import doore.member.domain.Participant;
import doore.member.domain.StudyRole;
import doore.member.domain.repository.MemberRepository;
import doore.member.domain.repository.ParticipantRepository;
import doore.member.domain.repository.StudyRoleRepository;
import doore.member.exception.MemberException;
import doore.study.application.dto.response.StudyReferenceResponse;
import doore.study.application.dto.response.StudyResponse;
import doore.study.domain.CurriculumItem;
import doore.study.domain.ParticipantCurriculumItem;
import doore.study.domain.Study;
import doore.study.domain.repository.CurriculumItemRepository;
import doore.study.domain.repository.ParticipantCurriculumItemRepository;
import doore.study.domain.repository.StudyRepository;
import doore.study.exception.StudyException;
import doore.team.domain.Team;
import doore.team.domain.TeamRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class StudyQueryServiceTest extends IntegrationTest {
    @Autowired
    private StudyCommandService studyCommandService;
    @Autowired
    private StudyQueryService studyQueryService;
    @Autowired
    private CurriculumItemCommandService curriculumItemCommandService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private StudyRepository studyRepository;
    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private CropRepository cropRepository;
    @Autowired
    private CurriculumItemRepository curriculumItemRepository;
    @Autowired
    private StudyRoleRepository studyRoleRepository;
    @Autowired
    private ParticipantCurriculumItemRepository participantCurriculumItemRepository;

    private Member member;
    private StudyRole studyRole;
    private Study study;
    private Team team;
    private Crop crop;


    @BeforeEach
    void setUp() {
        member = memberRepository.save(미나());
        team = teamRepository.save(team());
        crop = cropRepository.save(rice());
        study = studyRepository.save(algorithmStudy());
        studyRole = studyRoleRepository.save(StudyRole.builder()
                .studyRoleType(ROLE_스터디장)
                .memberId(member.getId())
                .studyId(study.getId())
                .build());
    }

    private StudyResponse getStudyResponse() {
        return StudyResponse.of(study, team, crop, 0, 1L);
    }

    @Nested
    @DisplayName("스터디 Query 테스트")
    class studyTest {
        @Test
        @DisplayName("[성공] 정상적으로 스터디 정보를 조회할 수 있다.")
        void findStudyById_정상적으로_스터디를_조회할_수_있다_성공() throws Exception {
            StudyResponse expectedResponse = getStudyResponse();
            StudyResponse actualResponse = studyQueryService.findStudyById(study.getId());

            assertThat(actualResponse)
                    .usingRecursiveComparison()
                    .ignoringCollectionOrder()
                    .isEqualTo(expectedResponse);
        }

        @Test
        @DisplayName("[실패] 존재하지 않는 스터디를 조회할 수 없다.")
        void findStudyById_존재하지_않는_스터디를_조회할_수_없다_실패() throws Exception {
            final Long notExistingStudyId = 0L;
            assertThatThrownBy(() -> studyQueryService.findStudyById(notExistingStudyId))
                    .isInstanceOf(StudyException.class)
                    .hasMessage(NOT_FOUND_STUDY.errorMessage());
        }
    }

    @Test
    @DisplayName("[성공] 내가 속한 스터디 목록을 조회할 수 있다.")
    void findMyStudies_내가_속한_스터디_목록을_조회할_수_있다_성공() {
        // given
        final Long tokenMemberId = member.getId();
        final Study anotherStudy = studyRepository.save(algorithmStudy());
        final Participant participantForStudy = participantRepository.save(
                Participant.builder().member(member).studyId(study.getId()).build());
        final Participant participantForAnotherStudy = participantRepository.save(
                Participant.builder().member(member).studyId(anotherStudy.getId()).build());

        final CurriculumItem curriculumItemForStudy1 = curriculumItemRepository.save(curriculumItem(study));
        final CurriculumItem curriculumItemForStudy2 = curriculumItemRepository.save(curriculumItem(study));
        final CurriculumItem curriculumItemForAnotherStudy = curriculumItemRepository.save(
                curriculumItem(anotherStudy));

        final ParticipantCurriculumItem participantCurriculumItem1 = participantCurriculumItemRepository.save(
                participantCurriculumItem(participantForStudy.getId(), curriculumItemForStudy1));
        final ParticipantCurriculumItem participantCurriculumItem2 = participantCurriculumItemRepository.save(
                participantCurriculumItem(participantForStudy.getId(), curriculumItemForStudy2));
        final ParticipantCurriculumItem participantCurriculumItem3 = participantCurriculumItemRepository.save(
                participantCurriculumItem(participantForAnotherStudy.getId(), curriculumItemForAnotherStudy));

        curriculumItemCommandService.checkCurriculum(curriculumItemForStudy1.getId(), participantForStudy.getId(),
                member.getId());

        // when
        final List<StudyReferenceResponse> expectedResponses = List.of(
                StudyReferenceResponse.of(study, 50),
                StudyReferenceResponse.of(anotherStudy, 0)
        );
        final List<StudyReferenceResponse> actualResponses = studyQueryService.findMyStudies(member.getId(),
                tokenMemberId);

        // then
        assertThat(participantCurriculumItem1.getIsChecked()).isEqualTo(true);
        assertThat(participantCurriculumItem2.getIsChecked()).isEqualTo(false);
        assertThat(participantCurriculumItem3.getIsChecked()).isEqualTo(false);
        assertThat(actualResponses)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResponses);
    }

    // TODO: 3/21/24 자기 자신이 아닌 사람의 스터디 목록을 조회하면 권한 예외가 발생한다. (2024/5/15 완료)
    @Test
    @DisplayName("[실패] 다른 사람의 스터디 목록 조회는 불가능하다.")
    void findMyStudy_다른_사람의_스터디_목록_조회는_불가능하다_실패() {
        final Long anotherMemberId = 2L;
        // 로그인 되어있는 아이디와 조회하려는 아이디가 다른 경우 실패 (주석은 확인 후 삭제할 예정입니다.)
        assertThatThrownBy(() -> {
            studyQueryService.findMyStudies(member.getId(), anotherMemberId);
        }).isInstanceOf(MemberException.class).hasMessage(UNAUTHORIZED.errorMessage());
    }
}
