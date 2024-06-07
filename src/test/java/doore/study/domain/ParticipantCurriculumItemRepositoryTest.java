package doore.study.domain;

import static doore.member.MemberFixture.아마란스;
import static doore.member.MemberFixture.아마어마어마;
import static doore.study.CurriculumItemFixture.curriculumItem;
import static doore.study.ParticipantCurriculumItemFixture.participantCurriculumItem;
import static doore.study.ParticipantFixture.participant;
import static doore.study.StudyFixture.algorithmStudy;

import doore.helper.RepositorySliceTest;
import doore.member.domain.Member;
import doore.member.domain.Participant;
import doore.member.domain.repository.MemberRepository;
import doore.member.domain.repository.ParticipantRepository;
import doore.study.domain.repository.CurriculumItemRepository;
import doore.study.domain.repository.ParticipantCurriculumItemRepository;
import doore.study.domain.repository.StudyRepository;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ParticipantCurriculumItemRepositoryTest extends RepositorySliceTest {
    @Autowired
    ParticipantCurriculumItemRepository participantCurriculumItemRepository;
    @Autowired
    StudyRepository studyRepository;
    @Autowired
    CurriculumItemRepository curriculumItemRepository;
    @Autowired
    ParticipantRepository participantRepository;
    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("[성공] 스터디 id와 회원 id를 사용해 participantCurriculumItem 목록을 조회할 수 있다.")
    public void findAllByStudyIdAndMemberId_스터디_id와_회원_id를_사용해_participantCurriculumItem_목록을_조회할_수_있다_성공()
            throws Exception {
        //given
        Member member = 아마란스();
        Member otherMember = 아마어마어마();
        memberRepository.saveAll(List.of(member, otherMember));

        Study study = algorithmStudy();
        Study otherStudy = algorithmStudy();
        studyRepository.saveAll(List.of(study, otherStudy));

        CurriculumItem curriculumItem = curriculumItem(study);
        CurriculumItem otherStudyCurriculumItem = curriculumItem(otherStudy);
        curriculumItemRepository.saveAll(List.of(curriculumItem, otherStudyCurriculumItem));

        Participant participant = participant(study.getId(), member);
        Participant otherParticipant = participant(study.getId(), otherMember);
        Participant participantFromOtherStudy = participant(otherStudy.getId(), member);
        participantRepository.saveAll(List.of(participant, otherParticipant, participantFromOtherStudy));

        ParticipantCurriculumItem participantCurriculumItem = participantCurriculumItem(participant.getId(),
                curriculumItem);
        ParticipantCurriculumItem otherParticipantCurriculumItem = participantCurriculumItem(otherParticipant.getId(),
                curriculumItem);
        ParticipantCurriculumItem otherStudyParticipantCurriculumItem = participantCurriculumItem(
                participantFromOtherStudy.getId(), otherStudyCurriculumItem);
        participantCurriculumItemRepository.saveAll(List.of(participantCurriculumItem, otherParticipantCurriculumItem,
                otherStudyParticipantCurriculumItem));

        //when
        List<ParticipantCurriculumItem> participantCurriculumItems = participantCurriculumItemRepository.findAllByStudyIdAndMemberId(
                study.getId(), 1L);

        //then
        Assertions.assertThat(participantCurriculumItems.size()).isEqualTo(1);
        Assertions.assertThat(participantCurriculumItems.get(0).getParticipantId()).isEqualTo(participant.getId());
        Assertions.assertThat(participantCurriculumItems.get(0).getCurriculumItem().getStudy().getId())
                .isEqualTo(study.getId());
    }
}
