package doore.document.api;

import doore.document.application.DocumentCommandService;
import doore.document.application.DocumentQueryService;
import doore.document.application.dto.request.DocumentCreateRequest;
import doore.document.application.dto.request.DocumentUpdateRequest;
import doore.document.application.dto.response.DocumentResponse;
import doore.document.domain.DocumentGroupType;
import doore.member.domain.Member;
import doore.resolver.LoginMember;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping
public class DocumentController {

    private final DocumentCommandService documentCommandService;
    private final DocumentQueryService documentQueryService;

    @PostMapping("/{groupType}/{groupId}/documents") // 회원
    public ResponseEntity<Void> createDocument(@Valid @RequestPart final DocumentCreateRequest request,
                                               @RequestPart(required = false) final List<MultipartFile> files,
                                               @PathVariable final String groupType,
                                               @PathVariable final Long groupId, @LoginMember final Member member) {
        final DocumentGroupType group = DocumentGroupType.value(groupType);
        documentCommandService.createDocument(request, files, group, groupId, member.getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{groupType}/{groupId}/documents") // 비회원
    public ResponseEntity<List<DocumentResponse>> getAllDocument(
            @PathVariable final String groupType,
            @PathVariable final Long groupId,
            @RequestParam(defaultValue = "0") @PositiveOrZero final int page,
            @RequestParam(defaultValue = "4") @PositiveOrZero final int size) {
        final DocumentGroupType group = DocumentGroupType.value(groupType);
        final List<DocumentResponse> documents =
                documentQueryService.getAllDocument(group, groupId, PageRequest.of(page, size));
        return ResponseEntity.status(HttpStatus.OK).body(documents);
    }

    @GetMapping("/{documentId}")  // 팀 학습자료 -> 비회원, 스터디 학습자료 -> 스터디 구성원
    public ResponseEntity<DocumentResponse> getDocument(@PathVariable final Long documentId,
                                                        @LoginMember final Member member) {
        final DocumentResponse response = documentQueryService.getDocument(documentId, member.getId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{documentId}") // 회원
    public ResponseEntity<Void> updateDocument(@Valid @RequestBody final DocumentUpdateRequest request,
                                               @PathVariable final Long documentId, @LoginMember final Member member) {
        documentCommandService.updateDocument(request, documentId, member.getId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{documentId}") // 회원
    public ResponseEntity<Void> deleteDocument(@PathVariable final Long documentId, @LoginMember final Member member) {
        documentCommandService.deleteDocument(documentId, member.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
