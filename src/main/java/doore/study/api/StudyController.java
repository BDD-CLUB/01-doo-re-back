package doore.study.api;

import doore.member.domain.Member;
import doore.resolver.LoginMember;
import doore.study.application.StudyCommandService;
import doore.study.application.StudyQueryService;
import doore.study.application.dto.request.StudyCreateRequest;
import doore.study.application.dto.request.StudyUpdateRequest;
import doore.study.application.dto.response.StudyRankResponse;
import doore.study.application.dto.response.StudyReferenceResponse;
import doore.study.application.dto.response.StudyResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
    public ResponseEntity<Void> createStudy(@Valid @RequestBody final StudyCreateRequest studyRequest,
                                            @PathVariable final Long teamId, @LoginMember final Member member) {
        studyCommandService.createStudy(studyRequest, teamId, member.getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/studies/{studyId}") // 스터디장
    public ResponseEntity<Void> deleteStudy(@PathVariable final Long studyId, @LoginMember final Member member) {
        studyCommandService.deleteStudy(studyId, member.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/studies/{studyId}") // 비회원
    public ResponseEntity<StudyResponse> getStudy(@PathVariable final Long studyId) {
        final StudyResponse studyDetailResponse = studyQueryService.findStudyById(studyId);
        return ResponseEntity.ok(studyDetailResponse);
    }

    @PutMapping("/studies/{studyId}") // 스터디장
    public ResponseEntity<Void> updateStudy(@Valid @RequestBody final StudyUpdateRequest studyUpdateRequest,
                                            @PathVariable final Long studyId, @LoginMember final Member member) {
        studyCommandService.updateStudy(studyUpdateRequest, studyId, member.getId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/studies/{studyId}/status") // 스터디장
    public ResponseEntity<Void> changeStudyStatus(@RequestParam final String status, @PathVariable final Long studyId,
                                                  @LoginMember final Member member) {
        studyCommandService.changeStudyStatus(status, studyId, member.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/studies/{studyId}/termination") // 스터디장
    public ResponseEntity<Void> terminateStudy(@PathVariable final Long studyId, @LoginMember final Member member) {
        studyCommandService.terminateStudy(studyId, member.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/studies/members/{memberId}") // 회원
    public ResponseEntity<List<StudyReferenceResponse>> getMyStudies(@PathVariable final Long memberId,
                                                                     @LoginMember final Member member) {
        // TODO: 3/22/24 토큰의 주인과 회원아이디가 같은지 검증 (2024/5/15 완료)
        return ResponseEntity.ok(studyQueryService.findMyStudies(memberId, member.getId()));
    }

    @GetMapping("/teams/{teamId}/studies") // 비회원
    public ResponseEntity<List<StudyRankResponse>> getTeamStudies(
            @PathVariable final Long teamId,
            @RequestParam(defaultValue = "0") @PositiveOrZero final int page,
            @RequestParam(defaultValue = "4") @PositiveOrZero final int size) {
        final List<StudyRankResponse> studyReferenceResponses =
                studyQueryService.getTeamStudies(teamId, PageRequest.of(page, size));
        return ResponseEntity.status(HttpStatus.OK).body(studyReferenceResponses);
    }
}
