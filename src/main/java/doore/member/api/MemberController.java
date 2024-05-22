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

    @PatchMapping("/teams/{teamId}/mandate/{newTeamLeaderId}") // 팀장
    public ResponseEntity<Void> transferTeamLeader(@PathVariable Long teamId, @PathVariable Long newTeamLeaderId,
                                                   @LoginMember Member member) {
        memberCommandService.transferTeamLeader(teamId, newTeamLeaderId, member.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/study/{studyId}/mandate/{newStudyLeaderId}") // 스터디장
    public ResponseEntity<Void> transferStudyLeader(@PathVariable Long studyId, @PathVariable Long newStudyLeaderId,
                                                    @LoginMember Member member) {
        memberCommandService.transferStudyLeader(studyId, newStudyLeaderId, member.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/members") // 회원
    public ResponseEntity<Void> deleteMember(@LoginMember Member member) {
        memberCommandService.deleteMember(member.getId());
        return ResponseEntity.noContent().build();
    }

}
