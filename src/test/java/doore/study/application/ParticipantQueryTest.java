package doore.study.application;

import static doore.member.MemberFixture.미나;
import static doore.member.MemberFixture.보름;
import static doore.member.MemberFixture.아마란스;
import static doore.member.exception.MemberExceptionType.UNAUTHORIZED;
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
import doore.member.domain.repository.ParticipantRepository;
import doore.member.domain.repository.StudyRoleRepository;
import doore.member.exception.MemberException;
import doore.study.application.dto.response.ParticipantResponse;
import doore.study.domain.Study;
import doore.study.domain.repository.StudyRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ParticipantQueryTest extends IntegrationTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private StudyRepository studyRepository;
    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired
    private ParticipantCommandService participantCommandService;
    @Autowired
    private ParticipantQueryService participantQueryService;
    @Autowired
    private StudyRoleRepository studyRoleRepository;

    private Member member;
    private Member otherMember;
    private Study study;
    private StudyRole studyRole;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(아마란스());
        otherMember = memberRepository.save(미나());
        study = studyRepository.save(algorithmStudy());
        studyRole = studyRoleRepository.save(StudyRole.builder()
                .studyRoleType(StudyRoleType.ROLE_스터디장)
                .studyId(study.getId())
                .memberId(member.getId())
                .build());
        studyRole = studyRoleRepository.save(StudyRole.builder()
                .studyRoleType(StudyRoleType.ROLE_스터디원)
                .studyId(study.getId())
                .memberId(otherMember.getId())
                .build());
    }

    @Test
    @DisplayName("[성공] 참여자를 정상적으로 조회할 수 있다.")
    void getParticipants_참여자를_정상적으로_조회할_수_있다_성공() {
        //given
        participantCommandService.createParticipant(study.getId(), member.getId(), member.getId());

        //when
        final List<ParticipantResponse> participantResponses = participantQueryService.getParticipants(study.getId(),
                member.getId());

        //then
        assertAll(
                () -> assertThat(participantResponses).hasSize(1),
                () -> assertEquals(member.getId(), participantResponses.get(0).memberId())
        );
    }

    @Test
    @DisplayName("[실패] 스터디_구성원이 아니라면 참여자를 조회할 수 없다.")
    void getParticipants_스터디_구성원이_아니라면_참여자를_조회할_수_없다_실패() throws Exception {
        final Member member = memberRepository.save(보름());

        assertThatThrownBy(() -> participantQueryService.getParticipants(study.getId(), member.getId()))
                .isInstanceOf(MemberException.class)
                .hasMessage(UNAUTHORIZED.errorMessage());
    }

    @Test
    @DisplayName("[성공] 참여자를 삭제하면 참여자 목록 조회에 삭제된 참여자가 조회되지 않는다.")
    void getParticipants_참여자를_삭제하면_참여자_목록_조회에_삭제된_참여자가_조회되지_않는다_성공() {
        participantRepository.save(Participant.builder().member(member).studyId(study.getId()).build());
        participantRepository.save(Participant.builder().member(otherMember).studyId(study.getId()).build());

        final List<ParticipantResponse> beforeParticipantResponses = participantQueryService.getParticipants(
                study.getId(), member.getId());

        participantCommandService.deleteParticipant(study.getId(), otherMember.getId(), member.getId());
        final List<ParticipantResponse> afterParticipantResponses = participantQueryService.getParticipants(
                study.getId(), member.getId());

        assertThat(beforeParticipantResponses.size()).isEqualTo(2);
        assertThat(afterParticipantResponses.size()).isEqualTo(1);
    }
}
