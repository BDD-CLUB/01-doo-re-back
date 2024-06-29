package doore.document.application;

import static doore.document.domain.DocumentGroupType.TEAM;
import static doore.document.domain.DocumentType.URL;
import static doore.member.MemberFixture.미나;
import static doore.member.MemberFixture.아마란스;
import static doore.member.MemberFixture.짱구;
import static doore.member.domain.StudyRoleType.ROLE_스터디원;
import static doore.study.StudyFixture.algorithmStudy;
import static doore.team.TeamFixture.team;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import doore.document.DocumentFixture;
import doore.document.application.dto.response.DocumentCondensedResponse;
import doore.document.application.dto.response.DocumentDetailResponse;
import doore.document.domain.Document;
import doore.document.domain.DocumentGroupType;
import doore.document.domain.repository.DocumentRepository;
import doore.helper.IntegrationTest;
import doore.member.domain.Member;
import doore.member.domain.StudyRole;
import doore.member.domain.repository.MemberRepository;
import doore.member.domain.repository.StudyRoleRepository;
import doore.study.domain.Study;
import doore.study.domain.repository.StudyRepository;
import doore.team.domain.Team;
import doore.team.domain.TeamRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

    private Study study;
    private Document document;
    private Document anotherDocument;
    private Member member;
    private Member anotherMember;
    private Member notMember;
    private StudyRole studyRole;
    private Team team;

    @BeforeEach
    void setUp() {
        team = teamRepository.save(team());
        study = studyRepository.save(algorithmStudy());
        member = memberRepository.save(아마란스());
        anotherMember = memberRepository.save(미나());
        notMember = 짱구();
        studyRole = studyRoleRepository.save(StudyRole.builder()
                .studyRoleType(ROLE_스터디원)
                .studyId(study.getId())
                .memberId(member.getId())
                .build());
        document = new DocumentFixture()
                .groupType(DocumentGroupType.STUDY)
                .groupId(study.getId())
                .type(URL)
                .uploaderId(member.getId())
                .buildDocument();
        anotherDocument = new DocumentFixture()
                .groupType(TEAM)
                .groupId(team.getId())
                .type(URL)
                .uploaderId(member.getId())
                .buildDocument();
    }

    @Test
    @DisplayName("[성공] 비회원이_정상적으로 팀 학습자료 목록을 조회할 수 있다")
    public void getAllDocumentList_비회원이_정상적으로_팀_학습자료_목록을_조회할_수_있다_성공() {
        //given&when
        final List<DocumentCondensedResponse> responses =
                documentQueryService.getAllDocument(TEAM, team.getId(), PageRequest.of(0, 4));

        //then
        assertAll(
                () -> assertThat(responses.size()).isNotZero(),
                () -> assertEquals(responses.get(0).title(), anotherDocument.getName()),
                () -> assertEquals(responses.get(0).description(), anotherDocument.getDescription()),
                () -> assertEquals(responses.get(0).date(), anotherDocument.getCreatedAt().toLocalDate()),
                () -> assertEquals(responses.get(0).uploaderId(), anotherDocument.getUploaderId())
        );
    }

    @Test
    @DisplayName("[성공] 정상적으로 팀 학습자료 상세를 조회할 수 있다.")
    public void getDocument_정상적으로_팀_학습자료_상세를_조회할_수_있다_성공() {
        //given&when
        final DocumentDetailResponse response = documentQueryService.getDocument(anotherDocument.getId(),
                notMember.getId());

        //then
        assertAll(
                () -> assertEquals(response.title(), anotherDocument.getName()),
                () -> assertEquals(response.description(), anotherDocument.getDescription()),
                () -> assertEquals(response.date(), anotherDocument.getCreatedAt().toLocalDate()),
                () -> assertEquals(response.accessType(), anotherDocument.getAccessType())
        );
    }

    @Test
    @DisplayName("[성공] 정상적으로 스터디 학습자료 상세를 조회할 수 있다")
    public void getDocument_정상적으로_스터디_학습자료_상세를_조회할_수_있다_성공() {
        //given&when
        final DocumentDetailResponse response = documentQueryService.getDocument(document.getId(), member.getId());

        //then
        assertAll(
                () -> assertEquals(response.title(), document.getName()),
                () -> assertEquals(response.description(), document.getDescription()),
                () -> assertEquals(response.date(), document.getCreatedAt().toLocalDate()),
                () -> assertEquals(response.accessType(), document.getAccessType())
        );
    }
}
