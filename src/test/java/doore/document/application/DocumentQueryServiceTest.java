package doore.document.application;

import static doore.member.MemberFixture.아마란스;
import static doore.study.StudyFixture.createStudy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import doore.document.DocumentFixture;
import doore.document.application.dto.response.DocumentCondensedResponse;
import doore.document.application.dto.response.DocumentDetailResponse;
import doore.document.domain.DocumentGroupType;
import doore.document.domain.DocumentType;
import doore.document.domain.Document;
import doore.document.domain.repository.DocumentRepository;
import doore.helper.IntegrationTest;
import doore.member.domain.Member;
import doore.member.domain.repository.MemberRepository;
import doore.study.domain.Study;
import doore.study.domain.repository.StudyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


public class DocumentQueryServiceTest extends IntegrationTest {

    @Autowired
    DocumentQueryService documentQueryService;
    @Autowired
    DocumentCommandService documentCommandService;

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    DocumentRepository documentRepository;

    @Autowired
    MemberRepository memberRepository;

    Study study;
    Document document;

    @BeforeEach
    void setUp() {
        study = createStudy();
        Member member = 아마란스();
        memberRepository.save(member);
        document = new DocumentFixture()
                .groupType(DocumentGroupType.STUDY)
                .groupId(study.getId())
                .type(DocumentType.URL)
                .uploaderId(member.getId())
                .buildDocument();
    }

    @Test
    @DisplayName("[성공] 정상적으로 학습자료 목록을 조회할 수 있다")
    public void getAllDocumentList_정상적으로_학습자료_목록을_조회할_수_있다_성공() {
        //given&when
        Page<DocumentCondensedResponse> responses =
                documentQueryService.getAllDocument(DocumentGroupType.STUDY, study.getId(), PageRequest.of(0, 4));

        //then
        assertAll(
                () -> assertThat(responses.getSize()).isNotZero(),
                () -> assertEquals(responses.getContent().get(0).title(), document.getName()),
                () -> assertEquals(responses.getContent().get(0).description(), document.getDescription()),
                () -> assertEquals(responses.getContent().get(0).date(), document.getCreatedAt().toLocalDate()),
                () -> assertEquals(responses.getContent().get(0).uploaderId(), document.getUploaderId())
        );
    }

    @Test
    @DisplayName("[성공] 정상적으로 학습자료 상세를 조회할 수 있다.")
    public void getDocument_정상적으로_학습자료_상세를_조회할_수_있다_성공() {
        //given&when
        DocumentDetailResponse response = documentQueryService.getDocument(document.getId());
        //then
        assertAll(
                () -> assertEquals(response.title(), document.getName()),
                () -> assertEquals(response.description(), document.getDescription()),
                () -> assertEquals(response.date(), document.getCreatedAt().toLocalDate()),
                () -> assertEquals(response.accessType(), document.getAccessType())
        );
    }
}
