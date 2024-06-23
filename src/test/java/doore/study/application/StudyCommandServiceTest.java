package doore.study.application;

import static doore.member.MemberFixture.미나;
import static doore.member.MemberFixture.보름;
import static doore.member.MemberFixture.아마란스;
import static doore.member.MemberFixture.짱구;
import static doore.member.domain.StudyRoleType.ROLE_스터디원;
import static doore.member.domain.StudyRoleType.ROLE_스터디장;
import static doore.member.exception.MemberExceptionType.UNAUTHORIZED;
import static doore.study.StudyFixture.algorithmStudy;
import static doore.study.domain.StudyStatus.ENDED;
import static doore.study.domain.StudyStatus.UPCOMING;
import static doore.study.exception.StudyExceptionType.INVALID_ENDDATE;
import static doore.study.exception.StudyExceptionType.NOT_FOUND_STATUS;
import static doore.study.exception.StudyExceptionType.NOT_FOUND_STUDY;
import static doore.team.TeamFixture.team;
import static doore.team.exception.TeamExceptionType.NOT_FOUND_TEAM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import doore.helper.IntegrationTest;
import doore.member.domain.Member;
import doore.member.domain.Participant;
import doore.member.domain.StudyRole;
import doore.member.domain.repository.MemberRepository;
import doore.member.domain.repository.ParticipantRepository;
import doore.member.domain.repository.StudyRoleRepository;
import doore.member.exception.MemberException;
import doore.study.application.dto.request.StudyCreateRequest;
import doore.study.application.dto.request.StudyUpdateRequest;
import doore.study.domain.CurriculumItem;
import doore.study.domain.ParticipantCurriculumItem;
import doore.study.domain.Study;
import doore.study.domain.StudyStatus;
import doore.study.domain.repository.CurriculumItemRepository;
import doore.study.domain.repository.ParticipantCurriculumItemRepository;
import doore.study.domain.repository.StudyRepository;
import doore.study.exception.StudyException;
import doore.team.domain.Team;
import doore.team.domain.TeamRepository;
import doore.team.exception.TeamException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class StudyCommandServiceTest extends IntegrationTest {
    @Autowired
    private StudyCommandService studyCommandService;
    @Autowired
    private StudyQueryService studyQueryService;
    @Autowired
    private StudyRepository studyRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private StudyRoleRepository studyRoleRepository;
    @Autowired
    private CurriculumItemRepository curriculumItemRepository;
    @Autowired
    private ParticipantCurriculumItemRepository participantCurriculumItemRepository;
    @Autowired
    private ParticipantRepository participantRepository;

    private Long memberId;
    private StudyRole studyRole;
    private Study study;

    @BeforeEach
    void setUp() {
        memberId = memberRepository.save(미나()).getId();
        study = studyRepository.save(algorithmStudy());
        studyRole = studyRoleRepository.save(StudyRole.builder()
                .studyRoleType(ROLE_스터디장)
                .studyId(study.getId())
                .memberId(memberId)
                .build());
    }

    @Nested
    @DisplayName("스터디 Command 테스트")
    class studyTest {
        @Nested
        @DisplayName("스터디 생성 테스트")
        class StudyCreateTest {
            StudyCreateRequest studyCreateRequest;
            Team team;

            @BeforeEach
            void setUp() {
                studyCreateRequest = StudyCreateRequest.builder()
                        .name("자바 스터디")
                        .description("자바 스터디 입니다")
                        .startDate(LocalDate.parse("2020-02-02"))
                        .endDate(null)
                        .cropId(1L)
                        .build();
                team = team();
                teamRepository.save(team);
            }

            @Test
            @DisplayName("[성공] 정상적으로 스터디를 생성할 수 있다.")
            void createStudy_정상적으로_스터디를_생성할_수_있다_성공() throws Exception {
                studyCommandService.createStudy(studyCreateRequest, team.getId(), memberId);
                final List<Study> studies = studyRepository.findAll();
                assertThat(studies).hasSize(2);
                final Study study = studies.get(1);
                assertEquals(study.getName(), studyCreateRequest.name());
                assertEquals(study.getDescription(), studyCreateRequest.description());
                assertEquals(study.getStartDate(), studyCreateRequest.startDate());
                assertEquals(study.getEndDate(), studyCreateRequest.endDate());
            }

            @Test
            @DisplayName("[성공] 스터디의 status와 isDeleted가 초기값으로 초기화 된다.")
            void createStudy_스터디의_status와_isDeleted가_초기값으로_초기화_된다_성공() throws Exception {
                studyCommandService.createStudy(studyCreateRequest, team.getId(), memberId);
                final List<Study> studies = studyRepository.findAll();
                final Study study = studies.get(1);
                assertAll(
                        () -> assertEquals(UPCOMING, study.getStatus()),
                        () -> assertEquals(false, study.getIsDeleted())
                );

            }

            @Test
            @DisplayName("[성공] 스터디 생성시 curriculum을 작성하지 않으면 빈 리스트로 생성된다.")
            void createStudy_스터디_생성시_curriculum을_작성하지_않으면_빈_리스트로_생성된다_성공() throws Exception {
                studyCommandService.createStudy(studyCreateRequest, team.getId(), memberId);
                final List<Study> studies = studyRepository.findAll();
                final Study study = studies.get(0);
                assertEquals(Collections.emptyList(), study.getCurriculumItems());
            }

            @Test
            @DisplayName("[실패] 스터디 종료일이 스터디 시작일보다 앞설 수 없다.")
            void createStudy_스터디_종료일이_스터디_시작일보다_앞설_수_없다_실패() throws Exception {
                final StudyCreateRequest wrongRequest = StudyCreateRequest.builder()
                        .name("스터디")
                        .description("스터디입니다.")
                        .startDate(LocalDate.parse("2020-02-02"))
                        .endDate(LocalDate.parse("2000-02-02"))
                        .cropId(1L)
                        .build();
                assertThatThrownBy(() -> studyCommandService.createStudy(wrongRequest, team.getId(), memberId))
                        .isInstanceOf(StudyException.class)
                        .hasMessage(INVALID_ENDDATE.errorMessage());
            }

            @Test
            @DisplayName("[성공] 스터디 생성자는 스터디장 권한이 부여된다.")
            void createStudy_스터디_생성자는_스터디장_권한이_부여된다_성공() throws Exception {
                studyCommandService.createStudy(studyCreateRequest, team.getId(), memberId);

                final StudyRole studyRole = studyRoleRepository.findById(memberId).orElseThrow();
                assertThat(studyRole.getStudyRoleType()).isEqualTo(ROLE_스터디장);
            }
        }

        @Nested
        @DisplayName("스터디 삭제 테스트")
        class StudyDeleteTest {
            private CurriculumItem curriculumItem1;
            private CurriculumItem curriculumItem2;
            private ParticipantCurriculumItem participantCurriculumItem1;
            private ParticipantCurriculumItem participantCurriculumItem2;
            private Participant participant1;
            private Participant participant2;
            private Member member1;
            private Member member2;

            @BeforeEach
            void setUp() {
                member1 = memberRepository.save(보름());
                member2 = memberRepository.save(짱구());
                participant1 = participantRepository.save(
                        Participant.builder().studyId(study.getId()).member(member1).build());
                participant2 = participantRepository.save(
                        Participant.builder().studyId(study.getId()).member(member2).build());
                curriculumItem1 = curriculumItemRepository.save(
                        CurriculumItem.builder().id(1L).name("커리1").itemOrder(1).study(study).build());
                curriculumItem2 = curriculumItemRepository.save(
                        CurriculumItem.builder().id(2L).name("커리2").itemOrder(2).study(study).build());
                participantCurriculumItem1 = participantCurriculumItemRepository.save(
                        ParticipantCurriculumItem.builder().participantId(member1.getId())
                                .curriculumItem(curriculumItem1)
                                .build());
                participantCurriculumItem2 = participantCurriculumItemRepository.save(
                        ParticipantCurriculumItem.builder().participantId(member2.getId())
                                .curriculumItem(curriculumItem2)
                                .build());
            }

            @Test
            @DisplayName("[성공] 정상적으로 스터디를 제할 수 있다.")
            void deleteStudy_정상적으로_스터디를_삭제할_수_있다_성공() throws Exception {
                studyCommandService.deleteStudy(study.getId(), memberId);
                final List<Study> studies = studyRepository.findAll();
                assertTrue(studies.get(0).getIsDeleted());
            }

            @Test
            @DisplayName("[성공] 스터디가 삭제되면 커리큘럼이 모두 삭제된다.")
            void deleteStudy_스터디가_삭제되면_커리큘럼이_모두_삭제된다_성공() throws Exception {
                final List<CurriculumItem> beforeCurriculumItems = curriculumItemRepository.findAllByStudyId(
                        study.getId());

                studyCommandService.deleteStudy(study.getId(), memberId);
                final List<CurriculumItem> afterCurriculumItems = curriculumItemRepository.findAllByStudyId(
                        study.getId());

                assertThat(beforeCurriculumItems.size()).isEqualTo(2);
                assertThat(afterCurriculumItems.get(0).getIsDeleted()).isEqualTo(true);
                assertThat(afterCurriculumItems.get(1).getIsDeleted()).isEqualTo(true);
            }

            @Test
            @DisplayName("[성공] 스터디가 삭제되면 참여자 커리큘럼이 모두 삭제된다.")
            void deleteStudy_스터디가_삭제되면_참여자_커리큘럼이_모두_삭제된다_성공() throws Exception {
                final List<ParticipantCurriculumItem> beforeParticipantCurriculumItem = participantCurriculumItemRepository.findAll();

                studyCommandService.deleteStudy(study.getId(), memberId);
                final List<ParticipantCurriculumItem> afterParticipantCurriculumItem = participantCurriculumItemRepository.findAll();

                assertThat(beforeParticipantCurriculumItem.size()).isEqualTo(2);
                assertThat(afterParticipantCurriculumItem).isEmpty();
            }
        }

        @Nested
        @DisplayName("스터디 종료 테스트")
        class studyTerminateTest {
            @Test
            @DisplayName("[성공] 정상적으로 스터디를 종료할 수 있다.")
            void terminateStudy_정상적으로_스터디를_종료할_수_있다_성공() throws Exception {
                final Study study = algorithmStudy();
                studyRepository.save(study);
                studyCommandService.terminateStudy(study.getId(), memberId);

                assertEquals(ENDED, study.getStatus());
            }

            @Test
            @DisplayName("[실패] 스터디장이 아니라면 스터디를 종료할 수 없다.")
            void terminateStudy_스터디장이_아니라면_스터디를_종료할_수_없다_실패() throws Exception {
                final Member member = memberRepository.save(아마란스());
                studyRoleRepository.save(StudyRole.builder()
                        .memberId(member.getId())
                        .studyId(study.getId())
                        .studyRoleType(ROLE_스터디원)
                        .build());

                assertThatThrownBy(() -> studyCommandService.terminateStudy(study.getId(), member.getId()))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(UNAUTHORIZED.errorMessage());
            }
        }

        @Nested
        @DisplayName("스터디 수정 테스트")
        class StudyupdateTest {
            final StudyUpdateRequest request = StudyUpdateRequest.builder()
                    .name("스프링")
                    .description("스프링 스터디 입니다.")
                    .startDate(LocalDate.parse("2023-01-01"))
                    .endDate(LocalDate.parse("2024-01-01"))
                    .status(StudyStatus.IN_PROGRESS)
                    .build();

            @Test
            @DisplayName("[성공] 정상적으로_스터디를_수정할_수_있다.")
            void updateStudy_정상적으로_스터디를_수정할_수_있다_성공() throws Exception {
                final Study study = algorithmStudy();
                studyRepository.save(study);
                studyCommandService.updateStudy(request, study.getId(), memberId);
                assertEquals(study.getName(), request.name());
            }

            @Test
            @DisplayName("[실패] 존재하지_않는_스터디를_수정할_수_없다.")
            void updateStudy_존재하지_않는_스터디를_수정할_수_없다_실패() throws Exception {
                final Long notExistingStudyId = 0L;
                assertThatThrownBy(() -> studyCommandService.updateStudy(request, notExistingStudyId, memberId))
                        .isInstanceOf(StudyException.class)
                        .hasMessage(NOT_FOUND_STUDY.errorMessage());
            }

            @Test
            @DisplayName("[실패] 스터디장이 아니라면 스터디를 수정할 수 없다.")
            void terminateStudy_스터디장이_아니라면_스터디를_수정할_수_없다_실패() throws Exception {
                final Member member = memberRepository.save(아마란스());
                studyRoleRepository.save(StudyRole.builder()
                        .memberId(member.getId())
                        .studyId(study.getId())
                        .studyRoleType(ROLE_스터디원)
                        .build());

                assertThatThrownBy(() -> studyCommandService.updateStudy(request, study.getId(), member.getId()))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(UNAUTHORIZED.errorMessage());
            }
        }

        @Nested
        @DisplayName("스터디 상태 수정 테스트")
        class StudyChangeStatusTest {
            @Test
            @DisplayName("[실패] 존재하지 않는 상태로 변경할 수 없다.")
            void changeStudyStatus_존재하지_않는_상태로_변경할_수_없다_실패() throws Exception {
                final Study study = algorithmStudy();
                studyRepository.save(study);
                assertThatThrownBy(
                        () -> studyCommandService.changeStudyStatus("NOT_EXISTING_STATUS", study.getId(), memberId))
                        .isInstanceOf(StudyException.class)
                        .hasMessage(NOT_FOUND_STATUS.errorMessage());
            }
        }
    }

    @Test
    @DisplayName("[실패] 존재하지 않는 스터디인 경우 실패한다.")
    void notExistStudy_존재하지_않는_스터디인_경우_실패한다_실패() {
        final Long notExistingStudyId = 50L;
        assertThatThrownBy(() -> studyCommandService.deleteStudy(notExistingStudyId, memberId))
                .isInstanceOf(StudyException.class)
                .hasMessage(NOT_FOUND_STUDY.errorMessage());
    }

    @Test
    @DisplayName("[실패] 존재하지 않는 팀인 경우 실패한다.")
    void notExistTeam_존재하지_않는_팀인_경우_실패한다_실패() {
        final Long notExistingTeamId = 50L;
        final StudyCreateRequest studyCreateRequest = mock(StudyCreateRequest.class);
        assertThatThrownBy(() -> studyCommandService.createStudy(studyCreateRequest, notExistingTeamId, memberId))
                .isInstanceOf(TeamException.class)
                .hasMessage(NOT_FOUND_TEAM.errorMessage());
    }
}
