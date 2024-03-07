package doore.study.application;

import static doore.member.MemberFixture.아마란스;
import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import doore.helper.IntegrationTest;
import doore.member.domain.Member;
import doore.member.domain.Participant;
import doore.member.domain.repository.MemberRepository;
import doore.member.domain.repository.ParticipantRepository;
import doore.member.exception.MemberException;
import doore.study.application.dto.request.StudyCreateRequest;
import doore.study.application.dto.request.StudyUpdateRequest;
import doore.study.domain.Study;
import doore.study.domain.StudyStatus;
import doore.study.domain.repository.StudyRepository;
import doore.study.exception.StudyException;
import doore.team.domain.Team;
import doore.team.domain.TeamRepository;
import jakarta.servlet.http.HttpServletRequest;
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
    StudyCommandService studyCommandService;
    @Autowired
    StudyQueryService studyQueryService;
    @Autowired
    StudyRepository studyRepository;
    @Autowired
    ParticipantRepository participantRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;

    @Nested
    @DisplayName("[스터디 Command 테스트")
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
                        .curriculumItems(null)
                        .build();
                team = team();
                teamRepository.save(team);
            }

            @Test
            @DisplayName("[성공] 정상적으로 스터디를 생성할 수 있다.")
            void createStudy_정상적으로_스터디를_생성할_수_있다_성공() throws Exception {
                studyCommandService.createStudy(studyCreateRequest, team.getId());
                List<Study> studies = studyRepository.findAll();
                assertThat(studies).hasSize(1);
                Study study = studies.get(0);
                assertEquals(study.getName(), studyCreateRequest.name());
                assertEquals(study.getDescription(), studyCreateRequest.description());
                assertEquals(study.getStartDate(), studyCreateRequest.startDate());
                assertEquals(study.getEndDate(), studyCreateRequest.endDate());
            }

            @Test
            @DisplayName("[성공] 스터디의 status와 isDeleted가 초기값으로 초기화 된다.")
            void createStudy_스터디의_status와_isDeleted가_초기값으로_초기화_된다_성공() throws Exception {
                studyCommandService.createStudy(studyCreateRequest, team.getId());
                List<Study> studies = studyRepository.findAll();
                Study study = studies.get(0);
                assertAll(
                        () -> assertEquals(UPCOMING, study.getStatus()),
                        () -> assertEquals(false, study.getIsDeleted())
                );

            }

            @Test
            @DisplayName("[성공] 스터디 생성시 curriculum을 작성하지 않으면 빈 리스트로 생성된다.")
            void createStudy_스터디_생성시_curriculum을_작성하지_않으면_빈_리스트로_생성된다_성공() throws Exception {
                studyCommandService.createStudy(studyCreateRequest, team.getId());
                List<Study> studies = studyRepository.findAll();
                Study study = studies.get(0);
                assertEquals(Collections.emptyList(), study.getCurriculumItems());
            }

            @Test
            @DisplayName("[실패] 스터디 종료일이 스터디 시작일보다 앞설 수 없다.")
            void createStudy_스터디_종료일이_스터디_시작일보다_앞설_수_없다_실패() throws Exception {
                StudyCreateRequest wrongRequest = StudyCreateRequest.builder()
                        .name("스터디")
                        .description("스터디입니다.")
                        .startDate(LocalDate.parse("2020-02-02"))
                        .endDate(LocalDate.parse("2000-02-02"))
                        .cropId(1L)
                        .curriculumItems(null)
                        .build();
                assertThatThrownBy(() -> studyCommandService.createStudy(wrongRequest, team.getId()))
                        .isInstanceOf(StudyException.class)
                        .hasMessage(INVALID_ENDDATE.errorMessage());
            }
        }

        @Test
        @DisplayName("[성공] 정상적으로 스터디를 삭제할 수 있다.")
        void deleteStudy_정상적으로_스터디를_삭제할_수_있다() throws Exception {
            Study study = algorithmStudy();
            studyRepository.save(study);
            studyCommandService.deleteStudy(study.getId());
            List<Study> studies = studyRepository.findAll();
            assertThat(studies).hasSize(0);
        }


        @Nested
        @DisplayName("스터디 종료 테스트")
        class StudyTerminatetest {
            @Test
            @DisplayName("[성공] 정상적으로 스터디를 종료할 수 있다.")
            void terminateStudy_정상적으로_스터디를_종료할_수_있다_성공() throws Exception {
                final Study study = algorithmStudy();
                studyRepository.save(study);
                studyCommandService.terminateStudy(study.getId());

                assertEquals(ENDED, study.getStatus());
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
                studyCommandService.updateStudy(request, study.getId());
                assertEquals(study.getName(), request.name());
            }

            @Test
            @DisplayName("[실패] 존재하지_않는_스터디를_수정할_수_없다.")
            void updateStudy_존재하지_않는_스터디를_수정할_수_없다_실패() throws Exception {
                Long notExistingStudyId = 0L;
                assertThatThrownBy(() -> studyCommandService.updateStudy(request, notExistingStudyId))
                        .isInstanceOf(StudyException.class)
                        .hasMessage(NOT_FOUND_STUDY.errorMessage());
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
                assertThatThrownBy(() -> studyCommandService.changeStudyStatus("NOT_EXISTING_STATUS", study.getId()))
                        .isInstanceOf(StudyException.class)
                        .hasMessage(NOT_FOUND_STATUS.errorMessage());
            }
        }
    }

    @Nested
    @DisplayName("참여자 Command 테스트")
    class participantTest {

        Member member;
        Study study;

        @BeforeEach
        void setUp() {
            member = 아마란스();
            study = algorithmStudy();
            memberRepository.save(member);
            studyRepository.save(study);
        }

        @Test
        @DisplayName("[성공] 정상적으로 참여자를 추가할 수 있다.")
        void saveParticipant_정상적으로_참여자를_추가할_수_있다_성공() {
            //Given
            Long studyId = study.getId();
            Long memberId = member.getId();

            //when
            studyCommandService.saveParticipant(studyId, memberId);

            //then
            List<Participant> participants = studyQueryService.findAllParticipants(studyId);
            assertAll(
                    () -> assertThat(participants).hasSize(1),
                    () -> assertEquals(memberId, participants.get(0).getMember().getId())
            );
        }

        @Test
        @DisplayName("[성공] 정상적으로 참여자를 삭제할 수 있다.")
        void deleteParticipant_정상적으로_참여자를_삭제할_수_있다_성공() {
            //Given
            Long studyId = study.getId();
            Long memberId = member.getId();
            studyCommandService.saveParticipant(studyId, memberId);

            //when
            studyCommandService.deleteParticipant(studyId, memberId);
            List<Participant> participants = studyQueryService.findAllParticipants(studyId);

            //then
            assertThat(participants).hasSize(0);
        }

        @Test
        @DisplayName("[성공] 정상적으로 참여자가 탈퇴 할 수 있다.")
        void withdrawParticipant_정상적으로_참여자가_탈퇴할_수_있다_성공() {
            //Given
            Long studyId = study.getId();
            Long memberId = member.getId();
            studyCommandService.saveParticipant(studyId, memberId);
            HttpServletRequest request = mock(HttpServletRequest.class);

            //when
            when(request.getHeader("Authorization")).thenReturn(String.valueOf(memberId));
            studyCommandService.withdrawParticipant(studyId, request);
            List<Participant> participants = studyQueryService.findAllParticipants(studyId);

            //then
            assertThat(participants).hasSize(0);

        }
    }

    @DisplayName("[실패] 존재하지 않는 회원인 경우 실패한다.")
    void notExistMember_존재하지_않는_회원인_경우_실패한다_실패() {
        Study study = algorithmStudy();
        studyRepository.save(study);
        Long notExistingMemberId = 50L;

        assertThatThrownBy(() -> studyCommandService.saveParticipant(study.getId(), notExistingMemberId))
                .isInstanceOf(MemberException.class)
                .hasMessage(NOT_FOUND_MEMBER.errorMessage());
    }

    @DisplayName("[실패] 존재하지 않는 스터디인 경우 실패한다.")
    void notExistStudy_존재하지_않는_스터디인_경우_실패한다_실패() {
        Long notExistingStudyId = 50L;
        assertThatThrownBy(() -> studyCommandService.deleteStudy(notExistingStudyId))
                .isInstanceOf(StudyException.class)
                .hasMessage(NOT_FOUND_STUDY.errorMessage());
    }

    @DisplayName("[실패] 존재하지 않는 팀인 경우 실패한다.")
    void notExistTeam_존재하지_않는_팀인_경우_실패한다_실패() {
        Long notExistingTeamId = 50L;
        StudyCreateRequest studyCreateRequest = mock(StudyCreateRequest.class);
        assertThatThrownBy(() -> studyCommandService.createStudy(studyCreateRequest, notExistingTeamId))
                .isInstanceOf(StudyException.class)
                .hasMessage(NOT_FOUND_TEAM.errorMessage());
    }
}
