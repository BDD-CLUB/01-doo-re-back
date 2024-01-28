package doore.study.api;

import doore.study.application.StudyCommandService;
import doore.study.application.StudyQueryService;
import doore.study.application.dto.request.StudyUpdateRequest;
import doore.study.application.dto.response.StudyDetailResponse;
import doore.study.application.dto.request.StudyCreateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class StudyController {
    private final StudyCommandService studyCommandService;
    private final StudyQueryService studyQueryService;

    @PostMapping("/teams/{teamId}/studies")
    public ResponseEntity<Void> createStudy(@Valid @RequestBody StudyCreateRequest studyRequest, @PathVariable Long teamId) {
        studyCommandService.createStudy(studyRequest, teamId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/studies/{studyId}")
    public ResponseEntity<Void> deleteStudy(@PathVariable Long studyId) {
        studyCommandService.deleteStudy(studyId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/studies/{studyId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<StudyDetailResponse> getStudy(@PathVariable Long studyId) {
        StudyDetailResponse studyDetailResponse = studyQueryService.findStudyById(studyId);
        return ResponseEntity.ok(studyDetailResponse);
    }

    @PutMapping("/studies/{studyId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> updateStudy(@Valid @RequestBody StudyUpdateRequest studyUpdateRequest, @PathVariable Long studyId) {
        studyCommandService.updateStudy(studyUpdateRequest, studyId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/studies/{studyId}/termination")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> terminateStudy(@PathVariable Long studyId) {
        studyCommandService.terminateStudy(studyId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
