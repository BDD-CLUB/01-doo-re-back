package doore.member.domain;

import static doore.member.domain.StudyRoleType.ROLE_스터디원;
import static org.assertj.core.api.Assertions.assertThat;

import doore.helper.RepositorySliceTest;
import doore.member.MemberFixture;
import doore.member.domain.repository.MemberRepository;
import doore.member.domain.repository.StudyRoleRepository;
import doore.study.StudyFixture;
import doore.study.domain.Study;
import doore.study.domain.repository.StudyRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class StudyRoleRepositoryTest extends RepositorySliceTest {

    @Autowired
    private StudyRoleRepository studyRoleRepository;
    @Autowired
    private StudyRepository studyRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("[성공] 스터디 정보와 멤버 정보가 유효하다면 스터디 내 역할을 조회할 수 있다")
    void findStudyRoleByStudyIdAndMemberId_스터디_정보와_멤버_정보가_유효하다면_스터디_내_역할을_조회할_수_있다() {
        Study study = StudyFixture.algorithmStudy();
        studyRepository.save(study);
        Member member = MemberFixture.아마란스();
        memberRepository.save(member);
        StudyRole studyRole = StudyRole.builder()
                .studyId(study.getId())
                .studyRoleType(ROLE_스터디원)
                .memberId(member.getId())
                .build();
        studyRoleRepository.save(studyRole);
        em.flush();
        em.clear();

        Optional<StudyRole> result = studyRoleRepository.findStudyRoleByStudyIdAndMemberId(study.getId(),
                member.getId());

        assertThat(result).isNotEmpty();
    }
}
