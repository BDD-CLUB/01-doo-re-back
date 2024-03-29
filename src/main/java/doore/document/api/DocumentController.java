package doore.document.api;

import static doore.document.exception.DocumentExceptionType.NOT_FOUND_GROUP_TYPE;

import doore.document.application.DocumentCommandService;
import doore.document.application.DocumentQueryService;
import doore.document.application.dto.request.DocumentCreateRequest;
import doore.document.application.dto.request.DocumentUpdateRequest;
import doore.document.application.dto.response.DocumentCondensedResponse;
import doore.document.application.dto.response.DocumentDetailResponse;
import doore.document.domain.DocumentGroupType;
import doore.document.exception.DocumentException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;
import javax.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    @PostMapping("/{groupType}/{groupId}/documents")
    public ResponseEntity<Void> createDocument(@Valid @RequestPart DocumentCreateRequest request,
                                               @RequestPart(required = false) final List<MultipartFile> files,
                                               @PathVariable String groupType,
                                               @PathVariable Long groupId) {
        DocumentGroupType group = DocumentGroupType.value(groupType);
        documentCommandService.createDocument(request, files, group, groupId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{groupType}/{groupId}/documents")
    public ResponseEntity<Page<DocumentCondensedResponse>> getAllDocument(
            @PathVariable String groupType,
            @PathVariable Long groupId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int page,
            @RequestParam(defaultValue = "4") @PositiveOrZero int size) {
        DocumentGroupType group = DocumentGroupType.value(groupType);
        Page<DocumentCondensedResponse> condensedDocuments =
                documentQueryService.getAllDocument(group, groupId, PageRequest.of(page, size));
        return ResponseEntity.status(HttpStatus.OK).body(condensedDocuments);
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<DocumentDetailResponse> getDocument(@PathVariable Long documentId) {
        DocumentDetailResponse response = documentQueryService.getDocument(documentId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{documentId}")
    public ResponseEntity<Void> updateDocument(@Valid @RequestBody DocumentUpdateRequest request,
                                               @PathVariable Long documentId) {
        documentCommandService.updateDocument(request, documentId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long documentId) {
        documentCommandService.deleteDocument(documentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
