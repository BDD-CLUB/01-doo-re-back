package doore.study.api;

import doore.study.application.dto.request.StudyUpdateRequest;
import doore.study.application.dto.response.StudyDetailResponse;
import doore.study.application.dto.request.StudyCreateRequest;
import doore.study.application.StudyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StudyController {
    private final StudyService studyService;

    @PostMapping("/teams/{teamId}/studies")
    @ResponseStatus(HttpStatus.CREATED)
    public void createStudy(@Valid @RequestBody StudyCreateRequest studyRequest, @PathVariable Long teamId) {
        studyService.createStudy(studyRequest, teamId);
    }

    @DeleteMapping("/studies/{studyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStudy(@PathVariable Long studyId) {
        studyService.deleteStudy(studyId);
    }

    @GetMapping("/studies/{studyId}")
    @ResponseStatus(HttpStatus.OK)
    public StudyDetailResponse getStudy(@PathVariable Long studyId) {
        return studyService.findStudyById(studyId);
    }

    @PatchMapping("/studies/{studyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateStudy(@Valid @RequestBody StudyUpdateRequest studyUpdateRequest, @PathVariable Long studyId) {
        studyService.updateStudy(studyUpdateRequest, studyId);
    }

    @PostMapping("/studies/{studyId}/termination")
    @ResponseStatus(HttpStatus.OK)
    public StudyDetailResponse terminateStudy(@PathVariable Long studyId) {
        return studyService.terminateStudy(studyId);
    }
}
