package doore.member.api;

import doore.member.application.MemberCommandService;
import doore.member.application.MemberQueryService;
import doore.member.application.dto.response.MemberAndMyTeamsAndStudiesResponse;
import doore.member.application.dto.response.MyPageUpdateRequest;
import doore.member.domain.Member;
import doore.resolver.LoginMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberCommandService memberCommandService;
    private final MemberQueryService memberQueryService;

    @PatchMapping("/teams/{teamId}/mandate/{newTeamLeaderId}") // 팀장
    public ResponseEntity<Void> transferTeamLeader(@PathVariable final Long teamId,
                                                   @PathVariable final Long newTeamLeaderId,
                                                   @LoginMember final Member member) {
        memberCommandService.transferTeamLeader(teamId, newTeamLeaderId, member.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/study/{studyId}/mandate/{newStudyLeaderId}") // 스터디장
    public ResponseEntity<Void> transferStudyLeader(@PathVariable final Long studyId,
                                                    @PathVariable final Long newStudyLeaderId,
                                                    @LoginMember final Member member) {
        memberCommandService.transferStudyLeader(studyId, newStudyLeaderId, member.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/members") // 회원
    public ResponseEntity<Void> deleteMember(@LoginMember final Member member) {
        memberCommandService.deleteMember(member.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/members/{memberId}") // 개인
    public ResponseEntity<MemberAndMyTeamsAndStudiesResponse> getSideBarInfo(@PathVariable final Long memberId,
                                                                             @LoginMember final Member member) {
        return ResponseEntity.ok(memberQueryService.getSideBarInfo(memberId, member.getId()));
    }

    @PatchMapping("/members/me") // 개인
    public ResponseEntity<Void> updateMyPage(@Valid @RequestBody final MyPageUpdateRequest request,
                                             @LoginMember final Member member) {
        memberCommandService.updateMyPage(request, member.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/members/me/image") // 개인
    public ResponseEntity<Void> updateMyPageImage(@RequestPart(required = false) final MultipartFile file,
                                                  @LoginMember final Member member) {
        memberCommandService.updateMyPageImage(file, member.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/members/me/image") // 개인
    public ResponseEntity<Void> deleteMyPageImage(@LoginMember final Member member) {
        memberCommandService.deleteMyPageImage(member.getId());
        return ResponseEntity.noContent().build();
    }
}
