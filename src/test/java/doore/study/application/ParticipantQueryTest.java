package doore.study.application;

import static doore.member.MemberFixture.보름;
import static doore.member.MemberFixture.아마란스;
import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER_ROLE_IN_STUDY;
import static doore.study.StudyFixture.algorithmStudy;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import doore.helper.IntegrationTest;
import doore.member.domain.Member;
import doore.member.domain.Participant;
import doore.member.domain.StudyRole;
import doore.member.domain.StudyRoleType;
import doore.member.domain.repository.MemberRepository;
import doore.member.domain.repository.StudyRoleRepository;
import doore.member.exception.MemberException;
import doore.study.domain.Study;
import doore.study.domain.repository.StudyRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ParticipantQueryTest extends IntegrationTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private StudyRepository studyRepository;
    @Autowired
    private ParticipantCommandService participantCommandService;
    @Autowired
    private ParticipantQueryService participantQueryService;
    @Autowired
    private StudyRoleRepository studyRoleRepository;

    private Member member;
    private Study study;
    private StudyRole studyRole;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(아마란스());
        study = studyRepository.save(algorithmStudy());
        studyRole = studyRoleRepository.save(StudyRole.builder()
                .studyRoleType(StudyRoleType.ROLE_스터디장)
                .studyId(study.getId())
                .memberId(member.getId())
                .build());
    }


    @Nested
    @DisplayName("참여자 Query 테스트")
    class participantTest {
        @Test
        @DisplayName("[성공] 참여자를 정상적으로 조회할 수 있다.")
        void findAllParticipants_참여자를_정상적으로_조회할_수_있다_성공() {
            //given
            participantCommandService.saveParticipant(study.getId(), member.getId(), member.getId());

            //when
            List<Participant> participants = participantQueryService.findAllParticipants(study.getId(), member.getId());

            //then
            assertAll(
                    () -> assertThat(participants).hasSize(1),
                    () -> assertEquals(member.getId(), participants.get(0).getMember().getId())
            );
        }

        @Test
        @DisplayName("[실패] 스터디_구성원이 아니라면 참여자를 조회할 수 없다.")
        void findAllParticipant_스터디_구성원이_아니라면_참여자를_조회할_수_없다_실패() throws Exception {
            Member member = memberRepository.save(보름());

            assertThatThrownBy(() -> participantQueryService.findAllParticipants(study.getId(), member.getId()))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(NOT_FOUND_MEMBER_ROLE_IN_STUDY.errorMessage());
        }
    }
}
