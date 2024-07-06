package doore.study.api;

import doore.member.domain.Member;
import doore.resolver.LoginMember;
import doore.study.application.CurriculumItemCommandService;
import doore.study.application.CurriculumItemQueryService;
import doore.study.application.dto.request.CurriculumItemManageRequest;
import doore.study.application.dto.response.CurriculumItemReferenceResponse;
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

    @PostMapping("/studies/{studyId}/curriculums") // 스터디장
    public ResponseEntity<Void> manageCurriculum(@PathVariable final Long studyId,
                                                 @Valid @RequestBody final CurriculumItemManageRequest request,
                                                 @LoginMember final Member member) {
        curriculumItemCommandService.manageCurriculum(request, studyId, member.getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/curriculums/{curriculumId}/{participantId}/check") // 스터디장 & 스터디원
    public ResponseEntity<Void> checkCurriculum(@PathVariable final Long curriculumId,
                                                @PathVariable final Long participantId,
                                                @LoginMember final Member member) {
        curriculumItemCommandService.checkCurriculum(curriculumId, participantId, member.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/studies/{studyId}/curriculums/all") // 비회원
    public ResponseEntity<List<CurriculumItemResponse>> getCurriculums(@PathVariable final Long studyId) {
        final List<CurriculumItemResponse> responses = curriculumItemQueryService.getCurriculums(studyId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/studies/{studyId}/curriculums") // 참여자
    public ResponseEntity<List<PersonalCurriculumItemResponse>> getMyCurriculum(@PathVariable final Long studyId,
                                                                                @LoginMember final Member member) {
        final List<PersonalCurriculumItemResponse> response = curriculumItemQueryService.getMyCurriculum(studyId, member.getId());
        return ResponseEntity.ok(response);
    }
}
