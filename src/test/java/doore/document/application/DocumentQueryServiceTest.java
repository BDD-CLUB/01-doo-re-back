package doore.document.application;

import static doore.document.domain.DocumentAccessType.ALL;
import static doore.document.domain.DocumentGroupType.STUDY;
import static doore.document.domain.DocumentGroupType.TEAM;
import static doore.document.domain.DocumentType.URL;
import static doore.member.MemberFixture.미나;
import static doore.member.MemberFixture.아마;
import static doore.member.MemberFixture.아마란스;
import static doore.member.domain.StudyRoleType.ROLE_스터디원;
import static doore.member.exception.MemberExceptionType.UNAUTHORIZED;
import static doore.study.StudyFixture.algorithmStudy;
import static doore.team.TeamFixture.team;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import doore.document.DocumentFixture;
import doore.document.application.dto.response.DocumentResponse;
import doore.document.domain.Document;
import doore.document.domain.DocumentAccessType;
import doore.document.domain.repository.DocumentRepository;
import doore.helper.IntegrationTest;
import doore.member.domain.Member;
import doore.member.domain.StudyRole;
import doore.member.domain.TeamRole;
import doore.member.domain.TeamRoleType;
import doore.member.domain.repository.MemberRepository;
import doore.member.domain.repository.StudyRoleRepository;
import doore.member.domain.repository.TeamRoleRepository;
import doore.member.exception.MemberException;
import doore.study.domain.Study;
import doore.study.domain.repository.StudyRepository;
import doore.team.domain.Team;
import doore.team.domain.repository.TeamRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


public class DocumentQueryServiceTest extends IntegrationTest {
    @Autowired
    private DocumentQueryService documentQueryService;
    @Autowired
    private DocumentCommandService documentCommandService;

    @Autowired
    private StudyRepository studyRepository;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private StudyRoleRepository studyRoleRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private TeamRoleRepository teamRoleRepository;

    private Study study;
    private Document studyDocument; // 스터디 학습자료
    private Document allOpenTeamDocument; // 전체 공개 팀 학습자료
    private Document teamOpenTeamDocument; // 팀 공개 팀 학습자료
    private Member participant;  // 회원 + 팀원 + 스터디원
    private Member notMemberTeamNotParticipantMember; // 회원
    private Member notParticipantMember; // 회원 + 팀원
    private StudyRole studyRole;
    private TeamRole teamRole;
    private Team team;

    @BeforeEach
    void setUp() {
        team = teamRepository.save(team());
        study = studyRepository.save(algorithmStudy());
        participant = memberRepository.save(아마란스());
        notMemberTeamNotParticipantMember = memberRepository.save(미나());
        notParticipantMember = memberRepository.save(아마());
        teamRole = teamRoleRepository.save(TeamRole.builder()
                .teamRoleType(TeamRoleType.ROLE_팀원)
                .memberId(notParticipantMember.getId())
                .teamId(study.getTeamId())
                .build());
        studyRole = studyRoleRepository.save(StudyRole.builder()
                .studyRoleType(ROLE_스터디원)
                .studyId(study.getId())
                .memberId(participant.getId())
                .build());
        studyDocument = new DocumentFixture()
                .groupType(STUDY)
                .groupId(study.getId())
                .type(URL)
                .uploaderId(participant.getId())
                .buildDocument();
        allOpenTeamDocument = new DocumentFixture()
                .groupType(TEAM)
                .groupId(team.getId())
                .type(URL)
                .accessType(ALL)
                .uploaderId(participant.getId())
                .buildDocument();
        teamOpenTeamDocument = new DocumentFixture()
                .groupType(TEAM)
                .groupId(team.getId())
                .type(URL)
                .accessType(DocumentAccessType.TEAM)
                .uploaderId(participant.getId())
                .buildDocument();
    }

    @Test
    @DisplayName("[성공] 비회원이_정상적으로 팀 학습자료 목록을 조회할 수 있다")
    public void getAllDocument_비회원이_정상적으로_팀_학습자료_목록을_조회할_수_있다_성공() {
        //given&when
        final Page<DocumentResponse> responses =
                documentQueryService.getAllDocument(TEAM, team.getId(), PageRequest.of(0, 4));
        final String uploaderName = memberRepository.findById(allOpenTeamDocument.getUploaderId()).orElseThrow()
                .getName();

        //then
        assertAll(
                () -> assertThat(responses.getTotalElements()).isNotZero(),
                () -> assertEquals(responses.getContent().get(0).title(), allOpenTeamDocument.getName()),
                () -> assertEquals(responses.getContent().get(0).description(), allOpenTeamDocument.getDescription()),
                () -> assertEquals(responses.getContent().get(0).date(),
                        allOpenTeamDocument.getCreatedAt().toLocalDate()),
                () -> assertEquals(responses.getContent().get(0).uploaderName(), uploaderName)
        );
    }

    @Test
    @DisplayName("[성공] 회원은 정상적으로 전체공개 팀 학습자료 상세를 조회할 수 있다.")
    public void getDocument_회원은_정상적으로_전체공개_팀_학습자료_상세를_조회할_수_있다_성공() {
        //given&when
        final DocumentResponse response = documentQueryService.getDocument(allOpenTeamDocument.getId(),
                notMemberTeamNotParticipantMember.getId());

        //then
        assertAll(
                () -> assertEquals(response.title(), allOpenTeamDocument.getName()),
                () -> assertEquals(response.description(), allOpenTeamDocument.getDescription()),
                () -> assertEquals(response.date(), allOpenTeamDocument.getCreatedAt().toLocalDate()),
                () -> assertEquals(response.accessType(), allOpenTeamDocument.getAccessType())
        );
    }

    @Test
    @DisplayName("[성공] 팀원은 정상적으로 팀공개 팀 학습자료 상세를 조회할 수 있다.")
    public void getDocument_팀원은_정상적으로_팀공개_팀_학습자료_상세를_조회할_수_있다_성공() {
        //given&when
        final DocumentResponse response = documentQueryService.getDocument(teamOpenTeamDocument.getId(),
                notParticipantMember.getId());

        //then
        assertAll(
                () -> assertEquals(response.title(), teamOpenTeamDocument.getName()),
                () -> assertEquals(response.description(), teamOpenTeamDocument.getDescription()),
                () -> assertEquals(response.date(), teamOpenTeamDocument.getCreatedAt().toLocalDate()),
                () -> assertEquals(response.accessType(), teamOpenTeamDocument.getAccessType())
        );
    }

    @Test
    @DisplayName("[실패] 회원은 팀공개 팀 학습자료 상세 조회를 할 수 없다.")
    public void getDocument_회원은_팀공개_팀_학습자료_상세_조회를_할_수_없다_실패() {
        assertThatThrownBy(
                () -> documentQueryService.getDocument(teamOpenTeamDocument.getId(),
                        notMemberTeamNotParticipantMember.getId()))
                .isInstanceOf(MemberException.class).hasMessage(UNAUTHORIZED.errorMessage());
    }

    @Test
    @DisplayName("[성공] 스터디원은 정상적으로 스터디 학습자료 상세를 조회할 수 있다")
    public void getDocument_스터디원은_정상적으로_스터디_학습자료_상세를_조회할_수_있다_성공() {
        //given&when
        final DocumentResponse response = documentQueryService.getDocument(studyDocument.getId(), participant.getId());

        //then
        assertAll(
                () -> assertEquals(response.title(), studyDocument.getName()),
                () -> assertEquals(response.description(), studyDocument.getDescription()),
                () -> assertEquals(response.date(), studyDocument.getCreatedAt().toLocalDate()),
                () -> assertEquals(response.accessType(), studyDocument.getAccessType())
        );
    }

    @Test
    @DisplayName("[실패] 회원은 스터디 학습자료 상세 조회를 할 수 없다.")
    public void getDocument_회원은_스터디_학습자료_상세_조회를_할_수_없다_실패() {
        assertThatThrownBy(
                () -> documentQueryService.getDocument(studyDocument.getId(),
                        notMemberTeamNotParticipantMember.getId()))
                .isInstanceOf(MemberException.class).hasMessage(UNAUTHORIZED.errorMessage());
    }

    @Test
    @DisplayName("[실패] 팀원은 스터디 학습자료 상세 조회를 할 수 없다.")
    public void getDocument_팀원은_스터디_학습자료_상세_조회를_할_수_없다_실패() {
        assertThatThrownBy(
                () -> documentQueryService.getDocument(studyDocument.getId(), notParticipantMember.getId()))
                .isInstanceOf(MemberException.class).hasMessage(UNAUTHORIZED.errorMessage());
    }

    @Test
    @DisplayName("[성공] 본인이 업로드한 학습자료 목록을 조회할 수 있다")
    public void getDocuments_본인이_업로드한_학습자료를_목록을_조회할_수_있다_성공() {
        final List<DocumentResponse> responses =
                documentQueryService.getDocuments(participant.getId());
        final String uploaderName = memberRepository.findById(allOpenTeamDocument.getUploaderId()).orElseThrow()
                .getName();

        assertThat(responses)
                .hasSize(3)
                .allSatisfy(response -> assertThat(response.uploaderName()).isEqualTo(uploaderName));
    }

    @Test
    @DisplayName("[성공] 내가 올린 학습자료가 아니라면 나의 학습자료에는 조회되지 않는다.")
    public void getDocuments_내가_올린_학습자료가_아니라면_나의_학습자료에는_조회되지_않는다_성공() {
        final List<DocumentResponse> responses =
                documentQueryService.getDocuments(notParticipantMember.getId());

        assertThat(responses).isEmpty();
    }
}
