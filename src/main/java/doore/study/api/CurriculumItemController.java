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
@RequestMapping("curriculums")
public class CurriculumItemController {

    private final CurriculumItemCommandService curriculumItemCommandService;

    @PostMapping
    public ResponseEntity<Void> createCurriculum(@Valid @RequestBody CurriculumItemRequest request) {
        curriculumItemCommandService.createCurriculum(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("{curriculumsId}")
    public ResponseEntity<Void> deleteCurriculum(@PathVariable Long curriculumsId) {
        curriculumItemCommandService.deleteCurriculum(curriculumsId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{curriculumsId}")
    public ResponseEntity<Void> updateCurriculum(@PathVariable Long curriculumsId,
                                                 @Valid @RequestBody CurriculumItemRequest request) {
        curriculumItemCommandService.updateCurriculum(curriculumsId, request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("{curriculumsId}")
    public ResponseEntity<Void> completeCurriculum(@PathVariable Long curriculumsId) {
        curriculumItemCommandService.completeCurriculum(curriculumsId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{curriculumsId}")
    public ResponseEntity<Void> incompleteCurriculum(@PathVariable Long curriculumsId) {
        curriculumItemCommandService.incompleteCurriculum(curriculumsId);
        return ResponseEntity.noContent().build();
    }


}
