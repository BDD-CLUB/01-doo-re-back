package doore.study.api;

import doore.member.domain.Participant;
import doore.study.application.ParticipantCommandService;
import doore.study.application.ParticipantQueryService;
import jakarta.servlet.http.HttpServletRequest;
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

    @PostMapping("/studies/{studyId}/members/{memberId}")
    public ResponseEntity<Void> saveParticipant(@PathVariable Long studyId, @PathVariable Long memberId) {
        participantCommandService.saveParticipant(studyId, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/studies/{studyId}/members/{memberId}")
    public ResponseEntity<Void> deleteParticipant(@PathVariable Long studyId, @PathVariable Long memberId) {
        participantCommandService.deleteParticipant(studyId, memberId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/studies/{studyId}/members")
    public ResponseEntity<Void> withdrawParticipant(@PathVariable Long studyId, HttpServletRequest request) {
        //Todo: 이후 권한 로직으로 수정
        participantCommandService.withdrawParticipant(studyId, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/studies/{studyId}/members")
    public ResponseEntity<List<Participant>> getParticipant(@PathVariable Long studyId) {
        List<Participant> participants = participantQueryService.findAllParticipants(studyId);
        return ResponseEntity.ok(participants);
    }
}
