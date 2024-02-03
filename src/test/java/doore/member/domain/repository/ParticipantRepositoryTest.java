package doore.member.domain.repository;

import static doore.member.MemberFixture.회원;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import doore.helper.RepositorySliceTest;
import doore.member.domain.Member;
import doore.member.domain.Participant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ParticipantRepositoryTest extends RepositorySliceTest {
    @Autowired
    ParticipantRepository participantRepository;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("[성공] 특정 스터디의 모든 참여자를 조회할 수 있다.")
    void findAllByStudyId_특정_스터디의_모든_참여자를_조회할_수_있다_성공() {
        //given
        Long studyId = 1L;
        Member member1 = Member.builder()
                .name("회원1")
                .email("이메일1")
                .imageUrl("url")
                .googleId("googleId1")
                .build();
        Member member2 = Member.builder()
                .name("회원2")
                .email("이메일2")
                .imageUrl("url")
                .googleId("googleId2")
                .build();
        memberRepository.save(member1);
        memberRepository.save(member2);
        Participant participant1 = Participant.builder()
                .member(member1)
                .studyId(studyId)
                .isCompleted(false)
                .isDeleted(false)
                .build();
        Participant participant2 = Participant.builder()
                .member(member2)
                .studyId(studyId)
                .isCompleted(false)
                .isDeleted(false)
                .build();
        participantRepository.save(participant1);
        participantRepository.save(participant2);

        //when
        List<Participant> participants = participantRepository.findAllByStudyId(studyId);

        //then
        final List<Participant> expectedParticipants = List.of(participant1, participant2);
        assertThat(participants)
                .usingRecursiveComparison()
                .isEqualTo(expectedParticipants);
    }

    @Test
    @DisplayName("[성공] 특정 스터디의 참여자를 삭제할 수 있다.")
    public void deleteByStudyIdAndMember_특정_스터디의_참여자를_삭제할_수_있다_성공() {
        //given
        Long studyId = 1L;
        Member member = 회원();
        memberRepository.save(member);
        Participant participant = Participant.builder()
                .member(member)
                .studyId(studyId)
                .isCompleted(false)
                .isDeleted(false)
                .build();
        participantRepository.save(participant);

        //when
        participantRepository.deleteByStudyIdAndMember(studyId, member);

        //then
        List<Participant> foundParticipant = participantRepository.findAllByStudyId(studyId);
        assertEquals(0,
                foundParticipant.stream()
                        .map(participant1 -> participant1.getMember().equals(member))
                        .toList()
                        .size()
        );
    }
}
