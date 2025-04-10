package doore.study.application;

import static doore.document.domain.DocumentGroupType.STUDY;
import static doore.document.domain.DocumentGroupType.TEAM;
import static doore.document.domain.DocumentType.URL;
import static doore.member.MemberFixture.미나;
import static doore.member.domain.StudyRoleType.ROLE_스터디장;
import static doore.member.exception.MemberExceptionType.UNAUTHORIZED;
import static doore.study.CurriculumItemFixture.curriculumItem;
import static doore.study.ParticipantCurriculumItemFixture.participantCurriculumItem;
import static doore.study.StudyFixture.algorithmStudy;
import static doore.study.StudyFixture.endedStudy;
import static doore.study.StudyFixture.inProgressStudy;
import static doore.study.StudyFixture.upComingStudy;
import static doore.study.exception.StudyExceptionType.NOT_FOUND_STUDY;
import static doore.team.TeamFixture.team;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import doore.document.DocumentFixture;
import doore.document.domain.Document;
import doore.document.domain.repository.DocumentRepository;
import doore.helper.IntegrationTest;
import doore.member.domain.Member;
import doore.member.domain.Participant;
import doore.member.domain.StudyRole;
import doore.member.domain.repository.MemberRepository;
import doore.member.domain.repository.ParticipantRepository;
import doore.member.domain.repository.StudyRoleRepository;
import doore.member.exception.MemberException;
import doore.study.application.dto.response.StudyRankResponse;
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
import doore.team.domain.repository.TeamRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class StudyQueryServiceTest extends IntegrationTest {
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
    private CurriculumItemRepository curriculumItemRepository;
    @Autowired
    private StudyRoleRepository studyRoleRepository;
    @Autowired
    private ParticipantCurriculumItemRepository participantCurriculumItemRepository;
    @Autowired
    private DocumentRepository documentRepository;

    private Member member;
    private StudyRole studyRole;
    private Study study;
    private Team team;


    @BeforeEach
    void setUp() {
        member = memberRepository.save(미나());
        team = teamRepository.save(team());
        study = studyRepository.save(algorithmStudy());
        studyRole = studyRoleRepository.save(StudyRole.builder()
                .studyRoleType(ROLE_스터디장)
                .memberId(member.getId())
                .studyId(study.getId())
                .build());
    }

    private StudyResponse getStudyResponse() {
        return StudyResponse.of(study, team, 0L, 1L);
    }

    @Nested
    @DisplayName("스터디 Query 테스트")
    class studyTest {
        @Test
        @DisplayName("[성공] 정상적으로 스터디 정보를 조회할 수 있다.")
        void getStudyById_정상적으로_스터디를_조회할_수_있다_성공() throws Exception {
            StudyResponse expectedResponse = getStudyResponse();
            StudyResponse actualResponse = studyQueryService.getStudy(study.getId());

            assertThat(actualResponse)
                    .usingRecursiveComparison()
                    .ignoringCollectionOrder()
                    .isEqualTo(expectedResponse);
        }

        @Test
        @DisplayName("[실패] 존재하지 않는 스터디를 조회할 수 없다.")
        void getStudyById_존재하지_않는_스터디를_조회할_수_없다_실패() throws Exception {
            final Long notExistingStudyId = 0L;
            assertThatThrownBy(() -> studyQueryService.getStudy(notExistingStudyId))
                    .isInstanceOf(StudyException.class)
                    .hasMessage(NOT_FOUND_STUDY.errorMessage());
        }
    }

    @Test
    @DisplayName("[성공] 내가 속한 스터디 목록을 조회할 수 있다.")
    void getMyStudies_내가_속한_스터디_목록을_조회할_수_있다_성공() {
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
        final List<StudyReferenceResponse> actualResponses = studyQueryService.getMyStudies(member.getId(),
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
    void getMyStudy_다른_사람의_스터디_목록_조회는_불가능하다_실패() {
        final Long anotherMemberId = 2L;
        assertThatThrownBy(() -> {
            studyQueryService.getMyStudies(member.getId(), anotherMemberId);
        }).isInstanceOf(MemberException.class).hasMessage(UNAUTHORIZED.errorMessage());
    }

    @Nested
    @DisplayName("스터디 정렬 테스트")
    class StudySortingTest {
        private Study inProgressHighPointStudy;
        private Study inProgressMediumPointStudy;
        private Study inProgressLowPointStudy;
        private Study upComingHighPointStudy;
        private Study upComingMediumPointStudy;
        private Study upComingLowPointStudy;
        private Study endedHighPointStudy;
        private Study endedMediumPointStudy;
        private Study endedLowPointStudy;

        private Document inProgressDocument;
        private Document upComingDocument;
        private Document endedDocument;

        private CurriculumItem inProgressCurriculumItem;
        private CurriculumItem upComingCurriculumItem;
        private CurriculumItem endedCurriculumItem;

        @BeforeEach
        void setUp() {
            studyRepository.deleteAll(); // "스터디 정렬 테스트"에서 생성된 스터디만 고려

            endedLowPointStudy = studyRepository.save(endedStudy());
            endedMediumPointStudy = studyRepository.save(endedStudy());
            endedHighPointStudy = studyRepository.save(endedStudy());

            inProgressLowPointStudy = studyRepository.save(inProgressStudy());
            inProgressMediumPointStudy = studyRepository.save(inProgressStudy());
            inProgressHighPointStudy = studyRepository.save(inProgressStudy());

            upComingLowPointStudy = studyRepository.save(upComingStudy());
            upComingMediumPointStudy = studyRepository.save(upComingStudy());
            upComingHighPointStudy = studyRepository.save(upComingStudy());

            inProgressDocument = new DocumentFixture()
                    .groupType(STUDY)
                    .groupId(inProgressMediumPointStudy.getId())
                    .type(URL)
                    .uploaderId(member.getId())
                    .buildDocument();
            upComingDocument = new DocumentFixture()
                    .groupType(STUDY)
                    .groupId(upComingMediumPointStudy.getId())
                    .type(URL)
                    .uploaderId(member.getId())
                    .buildDocument();
            endedDocument = new DocumentFixture()
                    .groupType(STUDY)
                    .groupId(endedMediumPointStudy.getId())
                    .type(URL)
                    .uploaderId(member.getId())
                    .buildDocument();

            participantRepository.save(
                    Participant.builder().studyId(inProgressHighPointStudy.getId()).member(member).build());
            participantRepository.save(
                    Participant.builder().studyId(upComingHighPointStudy.getId()).member(member).build());
            participantRepository.save(
                    Participant.builder().studyId(endedHighPointStudy.getId()).member(member).build());
            studyRoleRepository.save(
                    StudyRole.builder().studyRoleType(ROLE_스터디장).studyId(inProgressHighPointStudy.getId())
                            .memberId(member.getId()).build());
            studyRoleRepository.save(
                    StudyRole.builder().studyRoleType(ROLE_스터디장).studyId(upComingHighPointStudy.getId())
                            .memberId(member.getId()).build());
            studyRoleRepository.save(
                    StudyRole.builder().studyRoleType(ROLE_스터디장).studyId(endedHighPointStudy.getId())
                            .memberId(member.getId()).build());

            inProgressCurriculumItem = curriculumItemRepository.save(CurriculumItem.builder()
                    .study(inProgressHighPointStudy)
                    .itemOrder(1)
                    .name("IN-PROGRESS curriculum")
                    .build());
            upComingCurriculumItem = curriculumItemRepository.save(CurriculumItem.builder()
                    .study(upComingHighPointStudy)
                    .itemOrder(1)
                    .name("UPCOMING curriculum")
                    .build());
            endedCurriculumItem = curriculumItemRepository.save(CurriculumItem.builder()
                    .study(endedHighPointStudy)
                    .itemOrder(1)
                    .name("ENDED curriculum")
                    .build());

            participantCurriculumItemRepository.save(ParticipantCurriculumItem.builder()
                    .participantId(member.getId())
                    .curriculumItem(inProgressCurriculumItem)
                    .build());
            participantCurriculumItemRepository.save(ParticipantCurriculumItem.builder()
                    .participantId(member.getId())
                    .curriculumItem(upComingCurriculumItem)
                    .build());
            participantCurriculumItemRepository.save(ParticipantCurriculumItem.builder()
                    .participantId(member.getId())
                    .curriculumItem(endedCurriculumItem)
                    .build());
        }

        @Test
        @DisplayName("[성공] 스터디가 IN-PROGRESS, UPCOMING, ENDED 순으로 정렬된다.")
        void getTeamStudies_스터디가_IN_PROGRESS_UPCOMING_ENDED_순으로_정렬된다_성공() {
            final Pageable pageable = PageRequest.of(0, 10);
            final Page<StudyRankResponse> responses = studyQueryService.getTeamStudies(team.getId(), pageable);
            final List<StudyRankResponse> studyList = responses.getContent();

            assertThat(studyList.get(0).studyReferenceResponse().status()).isEqualTo(
                    inProgressMediumPointStudy.getStatus());
            assertThat(studyList.get(1).studyReferenceResponse().status()).isEqualTo(
                    inProgressLowPointStudy.getStatus());
            assertThat(studyList.get(2).studyReferenceResponse().status()).isEqualTo(
                    inProgressHighPointStudy.getStatus());

            assertThat(studyList.get(3).studyReferenceResponse().status()).isEqualTo(
                    upComingMediumPointStudy.getStatus());
            assertThat(studyList.get(4).studyReferenceResponse().status()).isEqualTo(
                    upComingLowPointStudy.getStatus());
            assertThat(studyList.get(5).studyReferenceResponse().status()).isEqualTo(
                    upComingHighPointStudy.getStatus());

            assertThat(studyList.get(6).studyReferenceResponse().status()).isEqualTo(
                    endedMediumPointStudy.getStatus());
            assertThat(studyList.get(7).studyReferenceResponse().status()).isEqualTo(
                    endedLowPointStudy.getStatus());
            assertThat(studyList.get(8).studyReferenceResponse().status()).isEqualTo(
                    endedHighPointStudy.getStatus());
        }

        @Test
        @DisplayName("[성공] 스터디가 상태 정렬 내에서는 활발도 기준으로 정렬된다.")
            // 커리큘럼 완성도 + 스터디 학습자료 개수
        void getTeamStudies_스터디가_상태_정렬_내에서는_활발도_기준으로_정렬된다_성공() {
            curriculumItemCommandService.checkCurriculum(inProgressCurriculumItem.getId(), member.getId(),
                    member.getId());
            curriculumItemCommandService.checkCurriculum(upComingCurriculumItem.getId(), member.getId(),
                    member.getId());
            curriculumItemCommandService.checkCurriculum(endedCurriculumItem.getId(), member.getId(),
                    member.getId());

            final Pageable pageable = PageRequest.of(0, 10);
            final Page<StudyRankResponse> responses = studyQueryService.getTeamStudies(team.getId(), pageable);
            final List<StudyRankResponse> studyList = responses.getContent();

            assertThat(studyList.get(0).studyReferenceResponse().id()).isEqualTo(
                    inProgressHighPointStudy.getId()); // 100
            assertThat(studyList.get(1).studyReferenceResponse().id()).isEqualTo(
                    inProgressMediumPointStudy.getId()); // 1
            assertThat(studyList.get(2).studyReferenceResponse().id()).isEqualTo(
                    inProgressLowPointStudy.getId()); // 0

            assertThat(studyList.get(3).studyReferenceResponse().id()).isEqualTo(
                    upComingHighPointStudy.getId()); // 100
            assertThat(studyList.get(4).studyReferenceResponse().id()).isEqualTo(
                    upComingMediumPointStudy.getId()); // 1
            assertThat(studyList.get(5).studyReferenceResponse().id()).isEqualTo(
                    upComingLowPointStudy.getId()); // 0

            assertThat(studyList.get(6).studyReferenceResponse().id()).isEqualTo(
                    endedHighPointStudy.getId()); // 100
            assertThat(studyList.get(7).studyReferenceResponse().id()).isEqualTo(
                    endedMediumPointStudy.getId()); // 1
            assertThat(studyList.get(8).studyReferenceResponse().id()).isEqualTo(
                    endedLowPointStudy.getId()); // 0
        }

        @Test
        @DisplayName("[성공] 커리큘럼 체크 개수를 기반으로 커리큘럼 진행도를 계산할 수 있다.")
        void checkStudyProgressRatio_커리큘럼_체크_개수를_기반으로_커리큘럼_진행도를_계산할_수_있다_성공() {
            final ParticipantCurriculumItem previousCurriculum = participantCurriculumItemRepository.findById(
                    inProgressCurriculumItem.getId()).orElseThrow();

            assertThat(previousCurriculum.getIsChecked()).isEqualTo(false);

            curriculumItemCommandService.checkCurriculum(inProgressCurriculumItem.getId(), member.getId(),
                    member.getId());
            final Pageable pageable = PageRequest.of(0, 10);
            final Page<StudyRankResponse> responses = studyQueryService.getTeamStudies(team.getId(), pageable);
            final List<StudyRankResponse> studyList = responses.getContent();
            final ParticipantCurriculumItem afterCurriculum = participantCurriculumItemRepository.findById(
                    inProgressCurriculumItem.getId()).orElseThrow();

            assertThat(afterCurriculum.getIsChecked()).isEqualTo(true);
            assertThat(studyList.get(0).studyReferenceResponse().id()).isEqualTo(inProgressHighPointStudy.getId());
            assertThat(studyList.get(0).point()).isEqualTo(100);
        }

        @Test
        @DisplayName("[성공] 커리큘럼 진행도와 스터디 학습자료 개수를 기반으로 포인트를 계산할 수 있다.")
        void calculatePoint_커리큘럼_진행도와_스터디_학습자료_개수를_기반으로_포인트를_계산할_수_있다_성공() {
            final Document otherInProgressDocument = new DocumentFixture()
                    .groupType(STUDY)
                    .groupId(inProgressHighPointStudy.getId())
                    .type(URL)
                    .uploaderId(member.getId())
                    .buildDocument();

            curriculumItemCommandService.checkCurriculum(inProgressCurriculumItem.getId(), member.getId(),
                    member.getId());
            final Pageable pageable = PageRequest.of(0, 4);
            final Page<StudyRankResponse> responses = studyQueryService.getTeamStudies(team.getId(), pageable);
            final List<StudyRankResponse> studyList = responses.getContent();
            final ParticipantCurriculumItem afterCurriculum = participantCurriculumItemRepository.findById(
                    inProgressCurriculumItem.getId()).orElseThrow();

            assertThat(afterCurriculum.getIsChecked()).isEqualTo(true);
            assertThat(studyList.get(0).point()).isEqualTo(101);
        }

        @Test
        @DisplayName("[성공] 팀 학습자료는 포인트에 합산되지 않는다.")
        void calculatePoint_팀_학습자료는_포인트에_합산되지_않는다_성공() {
            final Pageable pageable = PageRequest.of(0, 4);
            final Page<StudyRankResponse> beforeResponses = studyQueryService.getTeamStudies(team.getId(), pageable);
            final List<StudyRankResponse> beforeStudyList = beforeResponses.getContent();
            assertThat(beforeStudyList.get(0).studyReferenceResponse().id()).isEqualTo(
                    inProgressMediumPointStudy.getId());
            assertThat(beforeStudyList.get(0).point()).isEqualTo(1);

            final Document otherInProgressDocument = new DocumentFixture()
                    .groupType(TEAM)
                    .groupId(inProgressMediumPointStudy.getTeamId())
                    .type(URL)
                    .uploaderId(member.getId())
                    .buildDocument();

            final Page<StudyRankResponse> afterResponses = studyQueryService.getTeamStudies(team.getId(), pageable);
            final List<StudyRankResponse> afterStudyList = afterResponses.getContent();

            assertThat(afterStudyList.get(0).studyReferenceResponse().id()).isEqualTo(
                    inProgressMediumPointStudy.getId());
            assertThat(afterStudyList.get(0).point()).isEqualTo(1);
        }
    }
}
