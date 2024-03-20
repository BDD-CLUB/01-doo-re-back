package doore.study.api;

import doore.study.application.CurriculumItemCommandService;
import doore.study.application.dto.request.CurriculumItemManageRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

//    @Secured("ROLE_스터디장")
    @PostMapping("/studies/{studyId}/curriculums")
    public ResponseEntity<Void> manageCurriculum(@PathVariable Long studyId,
                                                 @Valid @RequestBody CurriculumItemManageRequest request) {
        curriculumItemCommandService.manageCurriculum(request, studyId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

//    @Secured({"ROLE_스터디장", "ROLE_스터디원"})
    @PatchMapping("/curriculums/{curriculumId}/{participantId}/check")
    public ResponseEntity<Void> checkCurriculum(@PathVariable Long curriculumId, @PathVariable Long participantId) {
        curriculumItemCommandService.checkCurriculum(curriculumId, participantId);
        return ResponseEntity.noContent().build();
    }

}
