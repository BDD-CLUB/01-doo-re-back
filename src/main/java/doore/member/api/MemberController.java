package doore.member.api;

import doore.member.application.MemberCommandService;
import doore.member.domain.MemberDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberCommandService memberCommandService;

    @PatchMapping("/teams/{teamsId}/mandate")
    public ResponseEntity<Void> transferTeamMaster(@PathVariable Long teamsId,
                                                   @AuthenticationPrincipal MemberDetails memberDetails) {
        //todo: 중복코드 효율적으로 없애기
        Long memberId = null;
        if (memberDetails != null) {
            memberId = memberDetails.getMember().getId();
        }
        memberCommandService.transferTeamMaster(teamsId, memberId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/study/{studyId}/mandate")
    public ResponseEntity<Void> transferStudyMaster(@PathVariable Long studyId,
                                                    @AuthenticationPrincipal MemberDetails memberDetails) {
        //todo: 중복코드 효율적으로 없애기
        Long memberId = null;
        if (memberDetails != null) {
            memberId = memberDetails.getMember().getId();
        }
        memberCommandService.transferStudyMaster(studyId, memberId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/members")
    public ResponseEntity<Void> deleteMember(@AuthenticationPrincipal MemberDetails memberDetails) {
        //todo: 중복코드 효율적으로 없애기
        Long memberId = null;
        if (memberDetails != null) {
            memberId = memberDetails.getMember().getId();
        }
        memberCommandService.deleteMember(memberId);
        return ResponseEntity.noContent().build();
    }
}
