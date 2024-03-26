package doore.study.domain;

import static doore.crop.CropFixture.rice;
import static doore.member.MemberFixture.아마란스;
import static doore.study.domain.StudyStatus.ENDED;
import static doore.team.TeamFixture.team;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import doore.helper.RepositorySliceTest;
import doore.member.domain.Member;
import doore.member.domain.Participant;
import doore.member.domain.repository.MemberRepository;
import doore.member.domain.repository.ParticipantRepository;
import doore.study.StudyFixture;
import doore.study.domain.repository.StudyRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class StudyRepositoryTest extends RepositorySliceTest {
    @Autowired
    StudyRepository studyRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ParticipantRepository participantRepository;

    @Test
    @DisplayName("정상적으로 특정 참여자를 가진 특정 상태의 스터디를 조회할 수 있다.")
    public void findAllByMemberIdAndStatus_정상적으로_특정_참여자를_가진_특정_상태의_스터디를_조회할_수_있다_성공() throws Exception {
        //given
        Study endedStudy = StudyFixture.builder().status(ENDED).studyBuild();
        Study inProgressedStudy = StudyFixture.builder().status(StudyStatus.IN_PROGRESS).studyBuild();
        Study notMyStudy = StudyFixture.builder().status(ENDED).studyBuild();
        Member member = 아마란스();
        memberRepository.save(member);

        Participant participantFromEndedStudy = Participant.builder()
                .studyId(endedStudy.getId())
                .member(member)
                .build();
        Participant participantFromInProgressedStudy = Participant.builder()
                .studyId(inProgressedStudy.getId())
                .member(member)
                .build();

        participantRepository.saveAll(List.of(participantFromEndedStudy, participantFromInProgressedStudy));

        em.flush();
        em.clear();

        //when
        List<Study> studies = studyRepository.findAllByMemberIdAndStatus(member.getId(), ENDED);

        //then
        assertAll(
                () -> assertThat(studies).hasSize(1),
                () -> assertEquals(ENDED, studies.get(0).getStatus())
        );
    }

}
