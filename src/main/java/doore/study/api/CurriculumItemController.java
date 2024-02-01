package doore.study.api;

import doore.study.application.CurriculumItemCommandService;
import doore.study.application.dto.request.CurriculumItemRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/studies/{studyId}/curriculums")
public class CurriculumItemController {

    private final CurriculumItemCommandService curriculumItemCommandService;

    @PostMapping
    public ResponseEntity<Void> createCurriculum(@PathVariable Long studyId,
                                                 @Valid @RequestBody CurriculumItemRequest request) {
        curriculumItemCommandService.createCurriculum(request, studyId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{curriculumsId}")
    public ResponseEntity<Void> deleteCurriculum(@PathVariable Long curriculumsId, @PathVariable Long studyId) {
        curriculumItemCommandService.deleteCurriculum(curriculumsId, studyId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{curriculumId}")
    public ResponseEntity<Void> updateCurriculum(@PathVariable Long curriculumId, @PathVariable Long studyId,
                                                 @Valid @RequestBody CurriculumItemRequest request) {
        curriculumItemCommandService.updateCurriculum(curriculumId, studyId, request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{curriculumId}/check")
    public ResponseEntity<Void> checkCurriculum(@PathVariable Long curriculumId, @PathVariable Long studyId) {
        curriculumItemCommandService.checkCurriculum(curriculumId, studyId);
        return ResponseEntity.noContent().build();
    }

}
