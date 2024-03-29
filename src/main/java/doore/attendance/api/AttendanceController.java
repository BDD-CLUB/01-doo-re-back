package doore.attendance.api;

import doore.attendance.application.AttendanceCommandService;
import doore.member.domain.Member;
import doore.resolver.LoginMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/attendances")
public class AttendanceController {

    private final AttendanceCommandService attendanceCommandService;

    @PostMapping
    public ResponseEntity<Void> createAttendance(@LoginMember Member member) {
        attendanceCommandService.createAttendance(member.getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
