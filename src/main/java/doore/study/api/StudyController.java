package doore.study.api;

import doore.member.domain.Participant;
import doore.study.application.StudyCommandService;
import doore.study.application.StudyQueryService;
import doore.study.application.dto.request.StudyCreateRequest;
import doore.study.application.dto.request.StudyUpdateRequest;
import doore.study.application.dto.response.StudyDetailResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<StudyDetailResponse> getStudy(@PathVariable Long studyId) {
        StudyDetailResponse studyDetailResponse = studyQueryService.findStudyById(studyId);
        return ResponseEntity.ok(studyDetailResponse);
    }

    @PutMapping("/studies/{studyId}")
    public ResponseEntity<Void> updateStudy(@Valid @RequestBody StudyUpdateRequest studyUpdateRequest, @PathVariable Long studyId) {
        studyCommandService.updateStudy(studyUpdateRequest, studyId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/studies/{studyId}/status")
    public ResponseEntity<Void> changeStudyStatus(@RequestParam String status, @PathVariable Long studyId) {
        studyCommandService.changeStudyStatus(status, studyId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/studies/{studyId}/termination")
    public ResponseEntity<Void> terminateStudy(@PathVariable Long studyId) {
        studyCommandService.terminateStudy(studyId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/studies/{studyId}/members/{memberId}")
    public ResponseEntity<Void> saveParticipant(@PathVariable Long studyId, @PathVariable Long memberId) {
        studyCommandService.saveParticipant(studyId, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/studies/{studyId}/members/{memberId}")
    public ResponseEntity<Void> deleteParticipant(@PathVariable Long studyId, @PathVariable Long memberId) {
        studyCommandService.deleteParticipant(studyId, memberId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/studies/{studyId}/members")
    public ResponseEntity<Void> withdrawParticipant(@PathVariable Long studyId, HttpSession session) {
        studyCommandService.withdrawParticipant(studyId, session);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/studies/{studyId}/members")
    public ResponseEntity<List<Participant>> getParticipant(@PathVariable Long studyId) {
        List<Participant> participants = studyQueryService.findAllParticipants(studyId);
        return ResponseEntity.ok(participants);
    }
}
