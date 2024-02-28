package doore.document.application;

import static doore.document.domain.DocumentType.*;
import static doore.document.exception.DocumentExceptionType.INVALID_EXTENSION;
import static doore.document.exception.DocumentExceptionType.NOT_FOUND_DOCUMENT;
import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;

import doore.document.application.dto.response.DocumentCondensedResponse;
import doore.document.application.dto.response.DocumentDetailResponse;
import doore.document.application.dto.response.FileResponse;
import doore.document.domain.StudyDocument;
import doore.document.exception.DocumentException;
import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import doore.document.domain.DocumentGroupType;
import doore.document.domain.DocumentType;
import doore.document.domain.File;
import doore.document.domain.repository.DocumentRepository;
import doore.member.domain.Member;
import doore.member.domain.repository.MemberRepository;
import doore.member.exception.MemberException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentQueryService {

    private final DocumentRepository documentRepository;
    private final MemberRepository memberRepository;

//    protected final String EXTENSION_DELIMITER = ".";

    public List<DocumentCondensedResponse> getAllDocument(DocumentGroupType groupType, Long groupId) {
        return documentRepository.findAllByGroupTypeAndGroupId(groupType, groupId).stream()
                .map(this::toDocumentCondensedResponse)
                .toList();
    }

    private DocumentCondensedResponse toDocumentCondensedResponse(StudyDocument document) {
        Long uploaderId = memberRepository.findById(document.getUploaderId())
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER)).getId();
//        String thumbnailUrl = getThumbnailUrl(document.getType(), document.getFiles().get(0));
        return new DocumentCondensedResponse(document.getId(), document.getName(), document.getDescription(), document.getCreatedAt().toLocalDate(), uploaderId);
    }

//    private String getThumbnailUrl(DocumentType type, File file) {
//        if (type.equals(image)) {
//            return file.getUrl();
//        }
//        if (type.equals(document)) {
//            final int extensionIndex = Objects.requireNonNull(file.getUrl())
//                    .lastIndexOf(EXTENSION_DELIMITER);
//            String documentExtension = file.getUrl().substring(extensionIndex);
//            return getExtensionThumbnail(documentExtension);
//        }
//        if (type.equals(url)) {
//            String crawlingUrl = file.getUrl();
//            try {
//                Document crawlingTarget = Jsoup.connect(crawlingUrl).get();
//                Element mainElement = crawlingTarget.getElementsByTag("main").first();
//                return mainElement.getElementsByTag("img").get(0).attr("src");
//            } catch (IOException e) {
//                return getExtensionThumbnail("link"); //todo: 링크 확장자 썸네일 반환
//                //test: 동적 페이지일 경우
//                //test: 정적 페이지 내에 이미지가 없을 경우
//            }
//        }
//        throw new DocumentException(INVALID_EXTENSION);
//    }
//
//    private String getExtensionThumbnail(String documentExtension) {
//        //todo: db 연동?
//        return "src/main/resources/images/thumbnail/zip.png";
//    }

    public DocumentDetailResponse getDocument(Long documentId) {
        StudyDocument studyDocument = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentException(NOT_FOUND_DOCUMENT));
        return toDocumentDetailResponse(studyDocument);
    }

    private DocumentDetailResponse toDocumentDetailResponse(StudyDocument document) {
        List<FileResponse> fileResponses = new ArrayList<>();
        for (File file : document.getFiles()) {
            FileResponse fileResponse = new FileResponse(file.getId(), file.getUrl());
            fileResponses.add(fileResponse);
        }

        Long uploaderId = memberRepository.findById(document.getUploaderId())
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER)).getId();
        return new DocumentDetailResponse(document.getId(), document.getName(), document.getDescription(),
                document.getAccessType(), document.getType(), fileResponses,
                document.getCreatedAt().toLocalDate(), uploaderId);
    }
}
