package doore.study.application;

import static doore.member.MemberFixture.아마란스;
import static doore.study.CurriculumItemFixture.curriculumItem;
import static doore.study.StudyFixture.createStudy;
import static doore.study.exception.StudyExceptionType.NOT_FOUND_STUDY;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import doore.crop.domain.Crop;
import doore.crop.domain.repository.CropRepository;
import doore.helper.IntegrationTest;
import doore.member.domain.Member;
import doore.member.domain.Participant;
import doore.member.domain.repository.MemberRepository;
import doore.member.domain.repository.ParticipantRepository;
import doore.study.application.dto.response.personalStudyResponse.PersonalStudyDetailResponse;
import doore.study.application.dto.response.totalStudyResponse.StudySimpleResponse;
import doore.study.domain.Study;
import doore.study.domain.repository.CurriculumItemRepository;
import doore.study.domain.repository.StudyRepository;
import doore.study.exception.StudyException;
import doore.team.domain.Team;
import doore.team.domain.TeamRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class StudyQueryServiceTest extends IntegrationTest {

    @Autowired
    StudyCommandService studyCommandService;
    @Autowired
    StudyQueryService studyQueryService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    StudyRepository studyRepository;
    @Autowired
    ParticipantRepository participantRepository;
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    CropRepository cropRepository;
    @Autowired
    CurriculumItemRepository curriculumItemRepository;
    @Autowired
    EntityManager em;

    @Nested
    @DisplayName("스터디 Query 테스트")
    class studyTest {
        @Test
        @DisplayName("[성공] 정상적으로 스터디 전체 정보를 조회할 수 있다.")
        void findStudyById_정상적으로_스터디를_조회할_수_있다_성공() throws Exception {
            Study study = createStudy();
            studyRepository.save(study);
            assertEquals(study.getId(), studyQueryService.findStudyById(study.getId()).id());
        }

        @Test
        @DisplayName("[성공] 정상적으로 스터디를 조회할 수 있다.")
        void getPersonalStudyDetail_정상적으로_스터디를_조회할_수_있다_성공() throws Exception {
            Study study = createStudy();
            Long memberId = 1L;
            studyRepository.save(study);
            PersonalStudyDetailResponse personalStudyDetailResponse =
                    studyQueryService.getPersonalStudyDetail(study.getId(), memberId);
            assertAll(
                    () -> assertEquals(study.getId(), personalStudyDetailResponse.id()),
                    () -> assertEquals(memberId, personalStudyDetailResponse.participantId())
            );
        }

        @Test
        @DisplayName("[실패] 존재하지 않는 스터디를 조회할 수 없다.")
        void findStudyById_존재하지_않는_스터디를_조회할_수_없다_실패() throws Exception {
            Long notExistingStudyId = 0L;
            assertThatThrownBy(() -> studyQueryService.findStudyById(notExistingStudyId))
                    .isInstanceOf(StudyException.class)
                    .hasMessage(NOT_FOUND_STUDY.errorMessage());
        }
    }

    @Test
    @DisplayName("[성공] 내가 속한 스터디 목록을 조회할 수 있다.")
    void findMyStudies_내가_속한_스터디_목록을_조회할_수_있다_성공() {
        // given
        final Study study = createStudy();
        final Study studyNotMine = createStudy();
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

        // when
        final List<StudySimpleResponse> actualResponses = studyQueryService.findMyStudies(member.getId());

        // then
        Assertions.assertThat(actualResponses)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResponses);
    }

    // TODO: 3/21/24 자기 자신이 아닌 사람의 스터디 목록을 조회하면 권한 예외가 발생한다.
}
