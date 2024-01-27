package doore.study.api;

import doore.study.application.StudyCommandService;
import doore.study.application.StudyQueryService;
import doore.study.application.dto.request.StudyUpdateRequest;
import doore.study.application.dto.response.StudyDetailResponse;
import doore.study.application.dto.request.StudyCreateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StudyController {
    private final StudyCommandService studyCommandService;
    private final StudyQueryService studyQueryService;

    @PostMapping("/teams/{teamId}/studies")
    @ResponseStatus(HttpStatus.CREATED)
    public void createStudy(@Valid @RequestBody StudyCreateRequest studyRequest, @PathVariable Long teamId) {
        studyCommandService.createStudy(studyRequest, teamId);
    }

    @DeleteMapping("/studies/{studyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStudy(@PathVariable Long studyId) {
        studyCommandService.deleteStudy(studyId);
    }

    @GetMapping("/studies/{studyId}")
    @ResponseStatus(HttpStatus.OK)
    public StudyDetailResponse getStudy(@PathVariable Long studyId) {
        return studyQueryService.findStudyById(studyId);
    }

    @PutMapping("/studies/{studyId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateStudy(@Valid @RequestBody StudyUpdateRequest studyUpdateRequest, @PathVariable Long studyId) {
        studyCommandService.updateStudy(studyUpdateRequest, studyId);
    }

    @PatchMapping("/studies/{studyId}/termination")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void terminateStudy(@PathVariable Long studyId) {
        studyCommandService.terminateStudy(studyId);
    }
}
