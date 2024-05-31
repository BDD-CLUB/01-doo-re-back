package doore.study.application;

import static doore.member.MemberFixture.미나;
import static doore.member.MemberFixture.아마란스;
import static doore.member.domain.StudyRoleType.ROLE_스터디장;
import static doore.member.exception.MemberExceptionType.UNAUTHORIZED;
import static doore.study.CurriculumItemFixture.curriculumItem;
import static doore.study.StudyFixture.algorithmStudy;
import static doore.study.StudyFixture.createStudy;
import static doore.study.exception.StudyExceptionType.NOT_FOUND_STUDY;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

import doore.study.application.dto.response.StudySimpleResponse;
import doore.study.domain.Study;
import doore.study.domain.repository.CurriculumItemRepository;
import doore.study.domain.repository.StudyRepository;
import doore.study.exception.StudyException;
import doore.team.domain.Team;
import doore.team.domain.TeamRepository;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

    private Long memberId;
    private StudyRole studyRole;
    private Study study;

    @BeforeEach
    void setUp() {
        memberId = memberRepository.save(미나()).getId();
        study = studyRepository.save(algorithmStudy());
        studyRole = studyRoleRepository.save(StudyRole.builder()
                .studyRoleType(ROLE_스터디장)
                .memberId(memberId)
                .studyId(study.getId())
                .build());
    }

    @Nested
    @DisplayName("스터디 Query 테스트")
    class studyTest {
        @Test
        @DisplayName("[성공] 정상적으로 스터디 정보를 조회할 수 있다.")
        void findStudyById_정상적으로_스터디를_조회할_수_있다_성공() throws Exception {
            Study study = createStudy();
            studyRepository.save(study);
            assertEquals(study.getId(), studyQueryService.findStudyById(study.getId(), memberId).id());
        }

        @Test
        @DisplayName("[실패] 존재하지 않는 스터디를 조회할 수 없다.")
        void findStudyById_존재하지_않는_스터디를_조회할_수_없다_실패() throws Exception {
            Long notExistingStudyId = 0L;
            assertThatThrownBy(() -> studyQueryService.findStudyById(notExistingStudyId, memberId))
                    .isInstanceOf(StudyException.class)
                    .hasMessage(NOT_FOUND_STUDY.errorMessage());
        }
    }

    @Test
    @Disabled //todo: 05/19/24 커리큘럼 & 스터디 수정되면 재수정
    @DisplayName("[성공] 내가 속한 스터디 목록을 조회할 수 있다.")
    void findMyStudies_내가_속한_스터디_목록을_조회할_수_있다_성공() {
        // given
        final Study studyWithCurriculum = studyRepository.findById(
                curriculumItemRepository.save(curriculumItem()).getStudy().getId()).get();
        final Team teamOfStudy = teamRepository.findById(study.getTeamId()).get();
        final Team teamOfStudyWithCurriculums = teamRepository.findById(studyWithCurriculum.getTeamId()).get();
        final Crop cropOfStudy = cropRepository.findById(study.getCropId()).get();
        final Crop cropOfStudyWithCurriculums = cropRepository.findById(studyWithCurriculum.getCropId()).get();
        final Member member = memberRepository.save(아마란스());
        participantRepository.save(Participant.builder()
                .studyId(study.getId())
                .member(member)
                .build());
        participantRepository.save(Participant.builder()
                .studyId(studyWithCurriculum.getId())
                .member(member)
                .build());
        final List<StudySimpleResponse> expectedResponses = List.of(
                StudySimpleResponse.of(study, teamOfStudy, cropOfStudy),
                StudySimpleResponse.of(studyWithCurriculum, teamOfStudyWithCurriculums, cropOfStudyWithCurriculums));
        final Long tokenMemberId = member.getId();

        // when
        final List<StudySimpleResponse> actualResponses = studyQueryService.findMyStudies(member.getId(),
                tokenMemberId);

        // then
        Assertions.assertThat(actualResponses)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResponses);
    }

    // TODO: 3/21/24 자기 자신이 아닌 사람의 스터디 목록을 조회하면 권한 예외가 발생한다. (2024/5/15 완료)
    @Test
    @DisplayName("[실패] 다른 사람의 스터디 목록 조회는 불가능하다.")
    void findMyStudy_다른_사람의_스터디_목록_조회는_불가능하다_실패() {
        Long anotherMemberId = 2L;
        // 로그인 되어있는 아이디와 조회하려는 아이디가 다른 경우 실패 (주석은 확인 후 삭제할 예정입니다.)
        assertThatThrownBy(() -> {
            studyQueryService.findMyStudies(memberId, anotherMemberId);
        }).isInstanceOf(MemberException.class).hasMessage(UNAUTHORIZED.errorMessage());
    }
}
