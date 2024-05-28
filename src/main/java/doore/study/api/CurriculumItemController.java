package doore.study.api;

import doore.study.application.CurriculumItemCommandService;
import doore.study.application.CurriculumItemQueryService;
import doore.study.application.dto.request.CurriculumItemManageRequest;
import doore.study.application.dto.response.CurriculumItemResponse;
import doore.study.application.dto.response.PersonalCurriculumItemResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class CurriculumItemController {

    private final CurriculumItemCommandService curriculumItemCommandService;
    private final CurriculumItemQueryService curriculumItemQueryService;

    @PostMapping("/studies/{studyId}/curriculums")
    public ResponseEntity<Void> manageCurriculum(@PathVariable Long studyId,
                                                 @Valid @RequestBody CurriculumItemManageRequest request) {
        curriculumItemCommandService.manageCurriculum(request, studyId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/curriculums/{curriculumId}/{participantId}/check")
    public ResponseEntity<Void> checkCurriculum(@PathVariable Long curriculumId, @PathVariable Long participantId) {
        curriculumItemCommandService.checkCurriculum(curriculumId, participantId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("studies/{studyId}/curriculums/all")
    public ResponseEntity<List<CurriculumItemResponse>> getCurriculums(@PathVariable Long studyId) {
        List<CurriculumItemResponse> responses = curriculumItemQueryService.getCurriculums(studyId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("studies/{studyId}/curriculums")
    public ResponseEntity<List<PersonalCurriculumItemResponse>> getMyCurriculum(@PathVariable Long studyId,
                                                                                HttpServletRequest request) {
        String memberId = request.getHeader("Authorization");
        List<PersonalCurriculumItemResponse> response = curriculumItemQueryService.getMyCurriculum(studyId, Long.parseLong(memberId));
        return ResponseEntity.ok(response);
    }
}
