package doore.study.api;

import doore.member.domain.Member;
import doore.member.domain.Participant;
import doore.resolver.LoginMember;
import doore.study.application.ParticipantCommandService;
import doore.study.application.ParticipantQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ParticipantController {
    private final ParticipantCommandService participantCommandService;
    private final ParticipantQueryService participantQueryService;

    @PostMapping("/studies/{studyId}/members/{memberId}") // 스터디장
    public ResponseEntity<Void> saveParticipant(@PathVariable final Long studyId, @PathVariable final Long memberId,
                                                @LoginMember final Member member) {
        participantCommandService.saveParticipant(studyId, memberId, member.getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/studies/{studyId}/members/{memberId}") // 스터디장
    public ResponseEntity<Void> deleteParticipant(@PathVariable final Long studyId, @PathVariable final Long memberId,
                                                  @LoginMember final Member member) {
        participantCommandService.deleteParticipant(studyId, memberId, member.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/studies/{studyId}/members") // 스터디원
    public ResponseEntity<Void> withdrawParticipant(@PathVariable final Long studyId,
                                                    @LoginMember final Member member) {
        participantCommandService.withdrawParticipant(studyId, member.getId(), member.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/studies/{studyId}/members") // 스터디장 & 스터디원
    public ResponseEntity<List<Participant>> getParticipant(@PathVariable final Long studyId,
                                                            @LoginMember final Member member) {
        final List<Participant> participants = participantQueryService.findAllParticipants(studyId, member.getId());
        return ResponseEntity.ok(participants);
    }
}
