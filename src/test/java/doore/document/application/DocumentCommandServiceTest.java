package doore.document.application;

import static doore.document.domain.DocumentGroupType.STUDY;
import static doore.document.exception.DocumentExceptionType.LINK_DOCUMENT_NEEDS_URL;
import static doore.document.exception.DocumentExceptionType.NO_FILE_ATTACHED;
import static doore.garden.domain.GardenType.DOCUMENT_UPLOAD;
import static doore.member.MemberFixture.createMember;
import static doore.member.MemberFixture.미나;
import static doore.member.exception.MemberExceptionType.UNAUTHORIZED;
import static doore.study.StudyFixture.algorithmStudy;
import static doore.study.StudyFixture.createStudy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

import doore.document.DocumentFixture;
import doore.document.application.dto.request.DocumentCreateRequest;
import doore.document.application.dto.request.DocumentUpdateRequest;
import doore.document.domain.Document;
import doore.document.domain.DocumentAccessType;
import doore.document.domain.DocumentType;
import doore.document.domain.repository.DocumentRepository;
import doore.document.domain.repository.FileRepository;
import doore.document.exception.DocumentException;
import doore.file.application.S3DocumentFileService;
import doore.file.application.S3ImageFileService;
import doore.garden.domain.Garden;
import doore.garden.domain.repository.GardenRepository;
import doore.helper.IntegrationTest;
import doore.member.domain.Member;
import doore.member.domain.repository.MemberRepository;
import doore.member.exception.MemberException;
import doore.study.domain.Study;
import doore.study.domain.repository.StudyRepository;
import doore.team.domain.TeamRepository;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class DocumentCommandServiceTest extends IntegrationTest {
    @Autowired
    private StudyRepository studyRepository;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private GardenRepository gardenRepository;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private S3ImageFileService s3ImageFileService;
    @Autowired
    private S3DocumentFileService s3DocumentFileService;
    @Autowired
    private DocumentCommandService documentCommandService;

    private DocumentCreateRequest documentRequest;
    private Study study;
    private Member member;

    @BeforeEach
    void setUp() {
        documentRequest = new DocumentCreateRequest("발표 자료", "이번주 발표자료입니다.", DocumentAccessType.TEAM,
                DocumentType.FILE, null, mock(Member.class).getId());
        study = createStudy();
        study = studyRepository.save(algorithmStudy());
        member = memberRepository.save(미나());
    }

    @Nested
    class createDocumentTest {
        @Disabled // S3 문제
        @Test
        @DisplayName("[성공] 정상적으로 파일 학습자료를 생성할 수 있다.")
        void createDocument_정상적으로_파일_학습자료를_생성할_수_있다_성공() throws IOException {
            // given
            final String folderName = "documents/";
            final String fileName = "document";
            final String contentType = "pdf";
            final String filePath = "src/test/resources/testDocument/document.pdf";
            FileInputStream fileInputStream = new FileInputStream(filePath);

            DocumentCreateRequest fileRequest = new DocumentCreateRequest("발표 자료", "이번주 발표자료입니다.",
                    DocumentAccessType.TEAM, DocumentType.FILE, null, mock(Member.class).getId());

            MultipartFile file = new MockMultipartFile(
                    fileName,
                    fileName + "." + contentType,
                    contentType,
                    fileInputStream);

            BDDMockito.given(s3DocumentFileService.upload(any()))
                    .willReturn(fileName);
            BDDMockito.given(s3ImageFileService.upload(any()))
                    .willReturn(fileName);

            // when
            documentCommandService.createDocument(fileRequest, List.of(file), STUDY,
                    study.getId(), member.getId());

            // then
            List<Document> documents = documentRepository.findAll();
            assertAll(
                    () -> assertThat(documents).hasSize(1),
                    () -> assertThat(documents.get(0).getFiles()).hasSize(1),
                    () -> assertEquals(documents.get(0).getName(), fileRequest.title())
            );
        }

        @Disabled // S3 문제
        @Test
        @DisplayName("[성공] 정상적으로 이미지 학습자료를 생성할 수 있다.")
        void createDocument_정상적으로_이미지_학습자료를_생성할_수_있다_성공() throws IOException {
            // given
            final String folderName = "images/";
            final String fileName = "testImage";
            final String contentType = "png";
            final String filePath = "src/test/resources/images/testImage.png";
            FileInputStream fileInputStream = new FileInputStream(filePath);

            DocumentCreateRequest imageRequest = new DocumentCreateRequest("강의 학습 인증", "강의 학습 인증샷입니다.",
                    DocumentAccessType.TEAM, DocumentType.IMAGE, null, mock(Member.class).getId());

            MultipartFile image = new MockMultipartFile(
                    fileName,
                    fileName + "." + contentType,
                    contentType,
                    fileInputStream);

            BDDMockito.given(s3DocumentFileService.upload(any()))
                    .willReturn(fileName);
            BDDMockito.given(s3ImageFileService.upload(any()))
                    .willReturn(fileName);

            // when
            documentCommandService.createDocument(imageRequest, List.of(image), STUDY,
                    study.getId(), member.getId());

            //then
            List<Document> document = documentRepository.findAll();
            assertAll(
                    () -> assertThat(document).hasSize(1),
                    () -> assertThat(document.get(0).getFiles()).hasSize(1),
                    () -> assertEquals(document.get(0).getName(), imageRequest.title())
            );
        }

        @Test
        @DisplayName("[성공] 정상적으로 링크 학습자료를 생성할 수 있다.")
        void createDocument_정상적으로_링크_학습자료를_생성할_수_있다_성공() {
            String urlPath = "https://github.com/BDD-CLUB";
            DocumentCreateRequest urlRequest = new DocumentCreateRequest("강의 정리", "강의 정리본입니다.",
                    DocumentAccessType.TEAM, DocumentType.URL, urlPath, mock(Member.class).getId());

            documentCommandService.createDocument(urlRequest, null, STUDY, study.getId(), member.getId());

            //then
            List<Document> document = documentRepository.findAll();
            assertAll(
                    () -> assertThat(document).hasSize(1),
                    () -> assertThat(document.get(0).getFiles()).hasSize(1),
                    () -> assertEquals(document.get(0).getFiles().get(0).getUrl(), urlPath),
                    () -> assertEquals(document.get(0).getName(), urlRequest.title())
            );
        }

        @Disabled // S3 문제
        @Test
        @DisplayName("[성공] 하나의 학습자료에 여러개의 파일을 업로드할 수 있다.")
        void createDocument_하나의_학습자료에_여러개의_파일을_업로드할_수_있다_성공() throws IOException {
            // given
            final String fileName = "testImage";
            final String contentType = "png";
            final String filePath = "src/test/resources/images/testImage.png";
            FileInputStream fileInputStream = new FileInputStream(filePath);
            FileInputStream fileInputStream2 = new FileInputStream(filePath);

            DocumentCreateRequest imageRequest = new DocumentCreateRequest("강의 학습 인증", "강의 학습 인증샷입니다.",
                    DocumentAccessType.TEAM,
                    DocumentType.IMAGE, null, mock(Member.class).getId());

            MultipartFile image = new MockMultipartFile(
                    fileName,
                    fileName + "." + contentType,
                    contentType,
                    fileInputStream);
            MultipartFile image2 = new MockMultipartFile(
                    fileName,
                    fileName + "." + contentType,
                    contentType,
                    fileInputStream2);

            BDDMockito.given(s3DocumentFileService.upload(any()))
                    .willReturn(fileName);
            BDDMockito.given(s3ImageFileService.upload(any()))
                    .willReturn(fileName);

            // when
            documentCommandService.createDocument(imageRequest, List.of(image, image2), STUDY,
                    study.getId(), member.getId());

            // then
            List<Document> document = documentRepository.findAll();
            assertAll(
                    () -> assertThat(document).hasSize(1),
                    () -> assertThat(document.get(0).getFiles()).hasSize(2)
            );
        }

        @Test
        @DisplayName("[실패] 링크 학습자료에는 링크를 입력해야 한다.")
        public void validateDocumentType_링크_학습자료에는_링크를_입력해야_한다_실패() {
            //given
            DocumentCreateRequest urlRequest = new DocumentCreateRequest("강의 정리", "강의 정리본입니다.",
                    DocumentAccessType.TEAM, DocumentType.URL, null, mock(Member.class).getId());

            //when&then
            assertThatThrownBy(() ->
                    documentCommandService.createDocument(urlRequest, null, STUDY, study.getId(), member.getId()))
                    .isInstanceOf(DocumentException.class)
                    .hasMessage(LINK_DOCUMENT_NEEDS_URL.errorMessage());
        }

        @Test
        @DisplayName("[실패] 파일과 이미지 학습자료에는 파일이 첨부돼야 한다.")
        public void 파일과_이미지_학습자료에는_파일이_첨부돼야_한다_실패() {
            //given
            DocumentCreateRequest ImageRequest = new DocumentCreateRequest("사진 자료", "사진 자료입니다.",
                    DocumentAccessType.TEAM, DocumentType.IMAGE, null, mock(Member.class).getId());

            //when&then
            assertThatThrownBy(() ->
                    documentCommandService.createDocument(ImageRequest, null, STUDY, study.getId(), member.getId()))
                    .isInstanceOf(DocumentException.class)
                    .hasMessage(NO_FILE_ATTACHED.errorMessage());
        }
    }

    @Test
    @DisplayName("[성공] 정상적으로 학습자료를 업데이트 할 수 있다.")
    void updateDocument_정상적으로_학습자료를_업데이트_할_수_있다_성공() {
        //given
        Document document = new DocumentFixture().buildDocument();

        //when
        DocumentUpdateRequest updatedRequest = new DocumentUpdateRequest("강의 학습 인증(수정)", "강의 학습 인증샷입니다. 수정",
                DocumentAccessType.ALL);
        documentCommandService.updateDocument(updatedRequest, document.getId(), member.getId());

        //then
        assertAll(
                () -> assertEquals(updatedRequest.title(), document.getName()),
                () -> assertEquals(updatedRequest.description(), document.getDescription()),
                () -> assertEquals(updatedRequest.accessType(), document.getAccessType())
        );
    }

    @Test
    @DisplayName("[실패] 해당 자료 업로더가 아니라면 업데이트 할 수 없다.")
    void updateDocument_해당_자료_업로더가_아니라면_업데이트_할_수_없다_실패() {
        Document document = new DocumentFixture().buildDocument();
        Member notUploader = createMember();

        DocumentUpdateRequest updatedRequest = new DocumentUpdateRequest("강의 학습 인증(수정)", "강의 학습 인증샷입니다. 수정",
                DocumentAccessType.ALL);

        assertThatThrownBy(() ->
                documentCommandService.updateDocument(updatedRequest, document.getId(), notUploader.getId()))
                .isInstanceOf(MemberException.class)
                .hasMessage(UNAUTHORIZED.errorMessage());
    }

    @Test
    @DisplayName("[성공] 학습자료를 정상적으로 삭제할 수 있다.")
    void deleteDocument_학습자료를_정상적으로_삭제할_수_있다() {
        //given
        Document document = new DocumentFixture().buildDocument();
        assertThat(documentRepository.findAll()).hasSize(1);
        //when
        documentCommandService.deleteDocument(document.getId(), member.getId());

        //then
        List<Document> documents = documentRepository.findAll();
        assertTrue(documents.get(0).getIsDeleted());
    }

    @Test
    @DisplayName("[실패] 해당 자료 업로더가 아니라면 삭제 할 수 없다.")
    void deleteDocument_해당_자료_업로더가_아니라면_삭제_할_수_없다_실패() {
        Document document = new DocumentFixture().buildDocument();
        Member notUploader = createMember();

        assertThatThrownBy(() ->
                documentCommandService.deleteDocument(document.getId(), notUploader.getId()))
                .isInstanceOf(MemberException.class)
                .hasMessage(UNAUTHORIZED.errorMessage());
    }

    @Test
    @DisplayName("[실패] 회원이 아니라면 학습자료를 등록할 수 없다.")
    void createDocument_회원이_아니라면_학습자료를_등록할_수_없다() {
        Long invalidMemberId = 10L;

        DocumentCreateRequest fileRequest = new DocumentCreateRequest("발표 자료", "이번주 발표자료입니다.",
                DocumentAccessType.TEAM, DocumentType.FILE, null, mock(Member.class).getId());

        assertThatThrownBy(() ->
                documentCommandService.createDocument(fileRequest, null, STUDY, study.getId(), invalidMemberId))
                .isInstanceOf(MemberException.class)
                .hasMessage(UNAUTHORIZED.errorMessage());
    }

    @Test
    @DisplayName("[성공] 학습자료 업로드시 정상적으로 텃밭을 생성할 수 있다.")
    public void createGarden_학습자료_업로드시_정상적으로_텃밭에_반영된다_성공() throws Exception {
        //given
        Document document = new DocumentFixture().buildDocument();

        //when
        documentCommandService.createGarden(document);

        //then
        Garden garden = gardenRepository.findAll().get(0);
        assertEquals(garden.getContributionId(), document.getId());
        assertEquals(garden.getType(), DOCUMENT_UPLOAD);
    }
}
