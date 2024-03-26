package doore.study.application;

import static doore.crop.CropFixture.rice;
import static doore.member.MemberFixture.아마란스;
import static doore.study.exception.StudyExceptionType.NOT_A_PARTICIPANT;
import static doore.study.exception.StudyExceptionType.NOT_ENDED_STUDY;
import static doore.team.TeamFixture.team;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import doore.helper.IntegrationTest;
import doore.member.domain.Member;
import doore.member.domain.Participant;
import doore.member.domain.repository.MemberRepository;
import doore.member.domain.repository.ParticipantRepository;
import doore.study.StudyFixture;
import doore.study.application.dto.response.personalStudyResponse.PersonalStudyDetailResponse;
import doore.study.domain.Study;
import doore.study.domain.StudyStatus;
import doore.study.domain.repository.StudyRepository;
import doore.study.exception.StudyException;
import doore.team.domain.TeamRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class StudyCardQueryServiceTest extends IntegrationTest {
    @Autowired
    StudyCardQueryService studyCardQueryService;
    @Autowired
    ParticipantRepository participantRepository;
    @Autowired
    StudyRepository studyRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;

    Long memberId;
    Long notEndedStudyId;
    Long endedStudyId;
    Long notMyStudyId;

    @BeforeEach
    void setUp() {
        Study notEndedstudy = StudyFixture.builder().status(StudyStatus.IN_PROGRESS).teamId(team().getId())
                .cropId(rice().getId()).studyBuild();
        Study endedStudy = StudyFixture.builder().status(StudyStatus.ENDED).teamId(team().getId())
                .cropId(rice().getId()).studyBuild();
        Study notMyStudy = StudyFixture.builder().status(StudyStatus.ENDED).teamId(team().getId())
                .cropId(rice().getId()).studyBuild();

        notEndedStudyId = notEndedstudy.getId();
        endedStudyId = endedStudy.getId();
        notMyStudyId = notMyStudy.getId();

        Member member = 아마란스();
        memberRepository.save(member);
        memberId = member.getId();

        Participant participantFromStudy = Participant.builder()
                .studyId(notEndedstudy.getId())
                .member(member)
                .build();
        Participant participantFromOtherStudy = Participant.builder()
                .studyId(endedStudy.getId())
                .member(member)
                .build();
        participantRepository.saveAll(List.of(participantFromStudy, participantFromOtherStudy));
    }

    @Test
    @DisplayName("스터디카드 목록을 정상적으로 조회할 수 있다.")
    public void getStudyCards_스터디카드_목록을_정상적으로_조회할_수_있다_성공() throws Exception {
        //when
        List<PersonalStudyDetailResponse> personalStudyDetailResponses = studyCardQueryService.getStudyCards(memberId);

        //then
        assertAll(
                () -> assertThat(personalStudyDetailResponses).hasSize(1),
                () -> assertEquals(endedStudyId, personalStudyDetailResponses.get(0).studyResponse().getId())
        );
    }

    @Test
    @DisplayName("스터디카드를 정상적으로 조회할 수 있다.")
    public void getStudyCard_스터디카드를_정상적으로_조회할_수_있다_성공() throws Exception {
        //when
        PersonalStudyDetailResponse response = studyCardQueryService.getStudyCard(endedStudyId, memberId);
        //then
        assertNotNull(response);
    }

    @Test
    @DisplayName("내가 속하지 않은 스터디 카드를 조회할 수 없다.")
    public void getStudyCard_내가_속하지_않은_스터디_카드를_조회할_수_없다_실패() throws Exception {
        //when & then
        assertThatThrownBy(() -> studyCardQueryService.getStudyCard(notMyStudyId, memberId))
                .isInstanceOf(StudyException.class)
                .hasMessage(NOT_A_PARTICIPANT.errorMessage());
    }

    @Test
    @DisplayName("종료되지 않은 스터디 카드를 조회할 수 없다.")
    public void getStudyCard_종료되지_않은_스터디_카드를_조회할_수_없다_실패() throws Exception {
        //when & then
        assertThatThrownBy(() -> studyCardQueryService.getStudyCard(notEndedStudyId, memberId))
                .isInstanceOf(StudyException.class)
                .hasMessage(NOT_ENDED_STUDY.errorMessage());
    }
}
