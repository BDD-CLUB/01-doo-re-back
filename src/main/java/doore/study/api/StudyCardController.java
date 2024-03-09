package doore.study.api;

import static doore.member.exception.MemberExceptionType.UNAUTHORIZED;

import doore.document.application.DocumentQueryService;
import doore.document.application.dto.response.DocumentDetailResponse;
import doore.member.exception.MemberException;
import doore.study.application.StudyCardQueryService;
import doore.study.application.dto.response.StudyCardResponse;
import doore.study.application.dto.response.personalStudyResponse.PersonalStudyDetailResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/studyCards")
public class StudyCardController {
    private final StudyCardQueryService studyCardQueryService;
    private final DocumentQueryService documentQueryService;

    @GetMapping
    public ResponseEntity<List<PersonalStudyDetailResponse>> getStudyCards(HttpServletRequest request) {
        String memberId = request.getHeader("Authorization");
        if (memberId == null) {
            throw new MemberException(UNAUTHORIZED);
        }
        List<PersonalStudyDetailResponse> personalStudyDetailResponses = studyCardQueryService.getStudyCards(Long.parseLong(memberId));
        return ResponseEntity.status(HttpStatus.OK).body(personalStudyDetailResponses);
    }

    @GetMapping("/selected")
    public ResponseEntity<List<StudyCardResponse>> getSelectedStudyCards(@RequestParam List<Long> studyCardIds, HttpServletRequest request) {
        String memberId = request.getHeader("Authorization");
        if (memberId == null) {
            throw new MemberException(UNAUTHORIZED);
        }

        List<StudyCardResponse> studyCardResponses = new ArrayList<>();
        for (Long id : studyCardIds) {
           StudyCardResponse studyCardResponse = toStudyCardResponse(id,Long.parseLong(memberId));
            studyCardResponses.add(studyCardResponse);
        }

        return ResponseEntity.status(HttpStatus.OK).body(studyCardResponses);
    }

    @GetMapping("/{studyCardId}")
    public ResponseEntity<StudyCardResponse> getStudyCard(
            @PathVariable Long studyCardId, HttpServletRequest request) {
        String memberId = request.getHeader("Authorization");
        if (memberId == null) {
            throw new MemberException(UNAUTHORIZED);
        }
        StudyCardResponse studyCardResponse = toStudyCardResponse(studyCardId, Long.parseLong(memberId));
        return ResponseEntity.status(HttpStatus.OK).body(studyCardResponse);
    }

    private StudyCardResponse toStudyCardResponse(Long studyCardId, Long memberId) {
        PersonalStudyDetailResponse studyResponses =
                studyCardQueryService.getStudyCard(studyCardId, memberId);
        List<DocumentDetailResponse> documentResponses =
                documentQueryService.getUploadedDocumentsByMember(memberId);
        return new StudyCardResponse(studyResponses, documentResponses);
    }
}
