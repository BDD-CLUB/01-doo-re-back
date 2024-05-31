package doore.study.api;

import doore.member.domain.Member;
import doore.resolver.LoginMember;

import doore.study.application.StudyCommandService;
import doore.study.application.StudyQueryService;
import doore.study.application.dto.request.StudyCreateRequest;

import doore.study.application.dto.request.StudyUpdateRequest;
import doore.study.application.dto.response.StudyResponse;
import doore.study.application.dto.response.StudySimpleResponse;
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

    @PostMapping("/teams/{teamId}/studies") //회원
    public ResponseEntity<Void> createStudy(@Valid @RequestBody StudyCreateRequest studyRequest,
                                            @PathVariable Long teamId, @LoginMember Member member) {
        studyCommandService.createStudy(studyRequest, teamId, member.getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/studies/{studyId}") // 스터디장
    public ResponseEntity<Void> deleteStudy(@PathVariable Long studyId, @LoginMember Member member) {
        studyCommandService.deleteStudy(studyId, member.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/studies/{studyId}")
    public ResponseEntity<StudyResponse> getStudy(@PathVariable Long studyId, @LoginMember Member member) {
        StudyResponse studyDetailResponse = studyQueryService.findStudyById(studyId, member.getId());
        return ResponseEntity.ok(studyDetailResponse);
    }

    @PutMapping("/studies/{studyId}") // 스터디장
    public ResponseEntity<Void> updateStudy(@Valid @RequestBody StudyUpdateRequest studyUpdateRequest,
                                            @PathVariable Long studyId, @LoginMember Member member) {
        studyCommandService.updateStudy(studyUpdateRequest, studyId, member.getId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/studies/{studyId}/status") // 스터디장
    public ResponseEntity<Void> changeStudyStatus(@RequestParam String status, @PathVariable Long studyId,
                                                  @LoginMember Member member) {
        studyCommandService.changeStudyStatus(status, studyId, member.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/studies/{studyId}/termination") // 스터디장
    public ResponseEntity<Void> terminateStudy(@PathVariable Long studyId, @LoginMember Member member) {
        studyCommandService.terminateStudy(studyId, member.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/studies/members/{memberId}") // 회원
    public ResponseEntity<List<StudySimpleResponse>> getMyStudies(@PathVariable final Long memberId,
                                                                  @LoginMember Member member) {
        // TODO: 3/22/24 토큰의 주인과 회원아이디가 같은지 검증 (2024/5/15 완료)
        return ResponseEntity.ok(studyQueryService.findMyStudies(memberId, member.getId()));
    }
}
