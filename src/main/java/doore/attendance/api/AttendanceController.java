package doore.attendance.api;

import doore.attendance.application.AttendanceCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/attendances")
public class AttendanceController {

    private final AttendanceCommandService attendanceCommandService;

    @PostMapping
    public ResponseEntity<Void> createAttendance(@RequestHeader("Authorization") String authorization) {
        Long memberId = Long.parseLong(authorization); //todo: 권한 적용
        attendanceCommandService.createAttendance(memberId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
