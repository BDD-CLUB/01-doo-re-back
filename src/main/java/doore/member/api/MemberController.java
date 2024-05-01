package doore.member.api;

import doore.member.application.MemberCommandService;
import doore.member.domain.Member;
import doore.resolver.LoginMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @PatchMapping("/teams/{teamsId}/mandate/{newTeamMasterId}")
    public ResponseEntity<Void> transferTeamMaster(@PathVariable Long teamsId, @PathVariable Long newTeamMasterId,
                                                   @LoginMember Member member) {
        memberCommandService.transferTeamMaster(teamsId, newTeamMasterId, member.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/study/{studyId}/mandate/{newStudyMasterId}")
    public ResponseEntity<Void> transferStudyMaster(@PathVariable Long studyId, @PathVariable Long newStudyMasterId,
                                                    @LoginMember Member member) {
        memberCommandService.transferStudyMaster(studyId, newStudyMasterId, member.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/members")
    public ResponseEntity<Void> deleteMember(@LoginMember Member member) {
        memberCommandService.deleteMember(member.getId());
        return ResponseEntity.noContent().build();
    }

}
