package doore.member.domain.repository;

import doore.helper.RepositorySliceTest;
import doore.member.domain.Member;
import doore.member.domain.MemberTeam;
import java.util.List;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberTeamRepositoryTest extends RepositorySliceTest {
    @Autowired
    private MemberTeamRepository memberTeamRepository;
    @Autowired
    private MemberRepository memberRepository;

    private Member 아마란스;
    private Member 아마어마어마;
    private Member 아마스;
    private Member 보름;
    private Member 짱구;
    private Member 비비아마;
    private Member 아마;

    @BeforeEach
    void setup() {
        아마란스 = memberRepository.save(Member.builder().name("아마란스").email("").imageUrl("").googleId("").build());
        아마어마어마 = memberRepository.save(Member.builder().name("아마어마어마").email("").imageUrl("").googleId("").build());
        아마스 = memberRepository.save(Member.builder().name("아마스").email("").imageUrl("").googleId("").build());
        보름 = memberRepository.save(Member.builder().name("보름").email("").imageUrl("").googleId("").build());
        짱구 = memberRepository.save(Member.builder().name("짱구").email("").imageUrl("").googleId("").build());
        비비아마 = memberRepository.save(Member.builder().name("비비아마").email("").imageUrl("").googleId("").build());
        아마 = memberRepository.save(Member.builder().name("아마").email("").imageUrl("").googleId("").build());

        memberTeamRepository.save(MemberTeam.builder().teamId(1L).member(아마란스).isDeleted(false).build());
        memberTeamRepository.save(MemberTeam.builder().teamId(1L).member(아마어마어마).isDeleted(false).build());
        memberTeamRepository.save(MemberTeam.builder().teamId(1L).member(아마스).isDeleted(false).build());
        memberTeamRepository.save(MemberTeam.builder().teamId(1L).member(보름).isDeleted(false).build());
        memberTeamRepository.save(MemberTeam.builder().teamId(1L).member(짱구).isDeleted(false).build());
        memberTeamRepository.save(MemberTeam.builder().teamId(1L).member(비비아마).isDeleted(false).build());
        memberTeamRepository.save(MemberTeam.builder().teamId(2L).member(아마).isDeleted(false).build());
    }

    @Test
    @DisplayName("[성공] 키워드를 입력받으면 이름 또는 이메일의 앞부분이 키워드와 일치하는 팀원들을 조회한다.")
    void findAllByTeamIdAndKeyword_키워드를_입력받으면_이름_또는_이메일의_앞부분이_키워드와_일치하는_팀원들을_조회한다_성공() {
        //given
        final List<Member> expend = List.of(아마란스, 아마어마어마, 아마스);

        //when
        final List<Member> actual = memberTeamRepository.findAllByTeamIdAndKeyword(1L, "아마")
                .stream()
                .map(MemberTeam::getMember)
                .collect(Collectors.toList());

        //then
        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expend);
    }
}
