package doore.document.application;

import static doore.document.domain.DocumentGroupType.STUDY;
import static doore.document.exception.DocumentExceptionType.LINK_DOCUMENT_NEEDS_FILE;
import static doore.document.exception.DocumentExceptionType.NO_FILE_ATTACHED;
import static doore.study.StudyFixture.algorithmStudy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import doore.document.DocumentFixture;
import doore.document.application.dto.request.DocumentCreateRequest;
import doore.document.application.dto.request.DocumentUpdateRequest;
import doore.document.domain.DocumentAccessType;
import doore.document.domain.DocumentType;
import doore.document.domain.StudyDocument;
import doore.document.domain.repository.DocumentRepository;
import doore.document.exception.DocumentException;
import doore.helper.IntegrationTest;
import doore.member.domain.Member;
import doore.study.domain.Study;
import doore.study.domain.repository.StudyRepository;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class DocumentCommandServiceTest extends IntegrationTest {

    @Autowired
    DocumentCommandService documentCommandService;

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    DocumentRepository documentRepository;

    DocumentCreateRequest documentRequest;
    Study study;

    @BeforeEach
    void setUp() {
        documentRequest = new DocumentCreateRequest("발표 자료", "이번주 발표자료입니다.", DocumentAccessType.TEAM,
                DocumentType.DOCUMENT, null, mock(Member.class).getId());
        study = algorithmStudy();
        studyRepository.save(study);
    }

    @Nested
    class createDocumentTest {
        @Test
        @DisplayName("[성공] 정상적으로 파일 학습자료를 생성할 수 있다.")
        void createDocument_정상적으로_파일_학습자료를_생성할_수_있다_성공() throws IOException {
            final String folderName = "documents/";
            final String fileName = "document";
            final String contentType = "pdf";
            final String filePath = "src/test/resources/testDocument/document.pdf";
            FileInputStream fileInputStream = new FileInputStream(filePath);

            DocumentCreateRequest fileRequest = new DocumentCreateRequest("발표 자료", "이번주 발표자료입니다.",
                    DocumentAccessType.TEAM, DocumentType.DOCUMENT, null, mock(Member.class).getId());

            MultipartFile document = new MockMultipartFile(
                    fileName,
                    fileName + "." + contentType,
                    contentType,
                    fileInputStream);

            documentCommandService.createDocument(fileRequest, List.of(document), STUDY,
                    study.getId());

            //then
            List<StudyDocument> studyDocument = documentRepository.findAll();
            assertAll(
                    () -> assertThat(studyDocument).hasSize(1),
                    () -> assertThat(studyDocument.get(0).getFiles()).hasSize(1),
                    () -> assertTrue(studyDocument.get(0).getFiles().get(0).getUrl().startsWith(folderName)),
                    () -> assertEquals(studyDocument.get(0).getName(), fileRequest.title())
            );
        }

        @Test
        @DisplayName("[성공] 정상적으로 이미지 학습자료를 생성할 수 있다.")
        void createDocument_정상적으로_이미지_학습자료를_생성할_수_있다_성공() throws IOException {
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

            documentCommandService.createDocument(imageRequest, List.of(image), STUDY,
                    study.getId());

            //then
            List<StudyDocument> studyDocument = documentRepository.findAll();
            assertAll(
                    () -> assertThat(studyDocument).hasSize(1),
                    () -> assertThat(studyDocument.get(0).getFiles()).hasSize(1),
                    () -> assertTrue(studyDocument.get(0).getFiles().get(0).getUrl().startsWith(folderName)),
                    () -> assertEquals(studyDocument.get(0).getName(), imageRequest.title())
            );
        }

        @Test
        @DisplayName("[성공] 정상적으로 링크 학습자료를 생성할 수 있다.")
        void createDocument_정상적으로_링크_학습자료를_생성할_수_있다_성공() {
            String urlPath = "https://github.com/BDD-CLUB";
            DocumentCreateRequest urlRequest = new DocumentCreateRequest("강의 정리", "강의 정리본입니다.",
                    DocumentAccessType.TEAM, DocumentType.URL, urlPath, mock(Member.class).getId());

            documentCommandService.createDocument(urlRequest, null, STUDY, study.getId());

            //then
            List<StudyDocument> studyDocument = documentRepository.findAll();
            assertAll(
                    () -> assertThat(studyDocument).hasSize(1),
                    () -> assertThat(studyDocument.get(0).getFiles()).hasSize(1),
                    () -> assertEquals(studyDocument.get(0).getFiles().get(0).getUrl(), urlPath),
                    () -> assertEquals(studyDocument.get(0).getName(), urlRequest.title())
            );
        }

        @Test
        @DisplayName("[성공] 하나의 학습자료에 여러개의 파일을 업로드할 수 있다.")
        void createDocument_하나의_학습자료에_여러개의_파일을_업로드할_수_있다_성공() throws IOException {
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

            documentCommandService.createDocument(imageRequest, List.of(image, image2), STUDY,
                    study.getId());

            //then
            List<StudyDocument> studyDocument = documentRepository.findAll();
            assertAll(
                    () -> assertThat(studyDocument).hasSize(1),
                    () -> assertThat(studyDocument.get(0).getFiles()).hasSize(2)
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
                    documentCommandService.createDocument(urlRequest, null, STUDY, study.getId()))
                    .isInstanceOf(DocumentException.class)
                    .hasMessage(LINK_DOCUMENT_NEEDS_FILE.errorMessage());
        }

        @Test
        @DisplayName("[실패] 파일과 이미지 학습자료에는 파일이 첨부돼야 한다.")
        public void 파일과_이미지_학습자료에는_파일이_첨부돼야_한다_실패() {
            //given
            DocumentCreateRequest ImageRequest = new DocumentCreateRequest("사진 자료", "사진 자료입니다.",
                    DocumentAccessType.TEAM, DocumentType.IMAGE, null, mock(Member.class).getId());

            //when&then
            assertThatThrownBy(() ->
                    documentCommandService.createDocument(ImageRequest, null, STUDY, study.getId()))
                    .isInstanceOf(DocumentException.class)
                    .hasMessage(NO_FILE_ATTACHED.errorMessage());
        }
    }


    @Test
    @DisplayName("[성공] 정상적으로 학습자료를 업데이트 할 수 있다.")
    void updateDocument_정상적으로_학습자료를_업데이트_할_수_있다_성공() {
        //given
        StudyDocument studyDocument = new DocumentFixture().buildStudyDocument();

        //when
        DocumentUpdateRequest updatedRequest = new DocumentUpdateRequest("강의 학습 인증(수정)", "강의 학습 인증샷입니다. 수정",
                DocumentAccessType.ALL);
        documentCommandService.updateDocument(updatedRequest, studyDocument.getId());

        //then
        assertAll(
                () -> assertEquals(updatedRequest.title(), studyDocument.getName()),
                () -> assertEquals(updatedRequest.description(), studyDocument.getDescription()),
                () -> assertEquals(updatedRequest.accessType(), studyDocument.getAccessType())
        );
    }


    @Test
    @DisplayName("[성공] 학습자료를 정상적으로 삭제할 수 있다.")
    void deleteDocument_학습자료를_정상적으로_삭제할_수_있다() {
        //given
        StudyDocument studyDocument = new DocumentFixture().buildStudyDocument();
        assertThat(documentRepository.findAll()).hasSize(1);
        //when
        documentCommandService.deleteDocument(studyDocument.getId());

        //then
        List<StudyDocument> studyDocuments = documentRepository.findAll();
        assertTrue(studyDocuments.get(0).getIsDeleted());
    }
}
