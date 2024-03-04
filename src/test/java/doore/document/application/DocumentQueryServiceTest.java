package doore.document.application;

import static doore.member.MemberFixture.회원;
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
        Member member = 회원();
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

//    @Nested
//    @DisplayName("학습자료 썸네일 테스트")
//    class DocumentThumbnailTest {
//        @Test
//        @DisplayName("[성공] 정상적으로 이미지 학습자료의 썸네일을 가져올 수 있다.")
//        public void getThumbnailUrl_정상적으로_이미지_학습자료의_썸네일을_가져올_수_있다_성공() throws Exception {
//            //given
//            final String fileName = "testImage";
//            final String contentType = "png";
//            final String filePath = "src/test/resources/images/testImage.png";
//            FileInputStream fileInputStream = new FileInputStream(filePath);
//
//            Member member = 회원();
//            memberRepository.save(member);
//
//            DocumentCreateRequest imageRequest = new DocumentCreateRequest("강의 학습 인증", "강의 학습 인증샷입니다.",
//                    DocumentAccessType.teams, DocumentType.image, null, member.getId());
//
//            MultipartFile image = new MockMultipartFile(
//                    fileName,
//                    fileName + "." + contentType,
//                    contentType,
//                    fileInputStream);
//
//            Study study = algorithmStudy();
//            studyRepository.save(study);
//            documentCommandService.createDocument(imageRequest, List.of(image), DocumentGroupType.studies,
//                    study.getId());
//
//            //when
//            List<DocumentCondensedResponse> documentCondensedResponses = documentQueryService.getAllDocumentList(
//                    DocumentGroupType.studies, study.getId());
//
//            //then
//            StudyDocument studyDocument = documentRepository.findAllByGroupTypeAndGroupId(DocumentGroupType.studies,
//                    study.getId()).get(0);
//            String url = documentCondensedResponses.get(0).thumbnailUrl();
//            assertEquals(studyDocument.getFiles().get(0).getUrl(), url);
//        }
//
//        @Test
//        @DisplayName("[성공] 정상적으로 링크 학습자료의 썸네일을 가져올 수 있다.")
//        public void getThumbnailUrl_정상적으로_링크_학습자료의_썸네일을_가져올_수_있다_성공() throws Exception {
//            //given
//            final String url = "https://en.wikipedia.org/wiki/Spring_Framework";
//            final String imageUrl = "//upload.wikimedia.org/wikipedia/commons/thumb/4/44/Spring_Framework_Logo_2018.svg/120px-Spring_Framework_Logo_2018.svg.png";
//            Member member = 회원();
//            memberRepository.save(member);
//
//            Study study = algorithmStudy();
//            studyRepository.save(study);
//
//            StudyDocument linkDocument = new DocumentFixture()
//                    .groupType(DocumentGroupType.studies)
//                    .groupId(study.getId())
//                    .type(DocumentType.url)
//                    .uploaderId(member.getId())
//                    .createLinkDocument(List.of(url));
//
//            //when
//            List<DocumentCondensedResponse> documentCondensedResponses = documentQueryService.getAllDocumentList(
//                    DocumentGroupType.studies, study.getId());
//
//            //then
//            String thumbnailUrl = documentCondensedResponses.get(0).thumbnailUrl();
//            assertEquals(imageUrl, thumbnailUrl);
//        }
//    }

}
