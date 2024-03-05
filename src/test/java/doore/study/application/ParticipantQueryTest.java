package doore.study.application;

import static doore.member.MemberFixture.아마란스;
import static doore.study.StudyFixture.algorithmStudy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import doore.helper.IntegrationTest;
import doore.member.domain.Member;
import doore.member.domain.Participant;
import doore.member.domain.repository.MemberRepository;
import doore.study.domain.Study;
import doore.study.domain.repository.StudyRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ParticipantQueryTest extends IntegrationTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    StudyRepository studyRepository;
    @Autowired
    ParticipantCommandService participantCommandService;
    @Autowired
    ParticipantQueryService participantQueryService;

    @Nested
    @DisplayName("참여자 Query 테스트")
    class participantTest {
        @Test
        @DisplayName("[성공] 참여자를 정상적으로 조회할 수 있다.")
        void findAllParticipants_참여자를_정상적으로_조회할_수_있다_성공() {
            //given
            Member member = 아마란스();
            memberRepository.save(member);
            Study study = algorithmStudy();
            studyRepository.save(study);
            participantCommandService.saveParticipant(study.getId(), member.getId());

            //when
            List<Participant> participants = participantQueryService.findAllParticipants(study.getId());

            //then
            assertAll(
                    () -> assertThat(participants).hasSize(1),
                    () -> assertEquals(member.getId(), participants.get(0).getMember().getId())
            );
        }
    }
}
