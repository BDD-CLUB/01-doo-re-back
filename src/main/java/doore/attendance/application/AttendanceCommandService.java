package doore.attendance.application;

import static doore.attendance.exception.AttendanceExceptionType.ALREADY_ATTENDED;

import doore.attendance.domain.Attendance;
import doore.attendance.domain.repository.AttendanceRepository;
import doore.attendance.exception.AttendanceException;
import doore.member.application.convenience.MemberValidateAccessPermission;
import doore.member.domain.Member;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AttendanceCommandService {

    private final AttendanceRepository attendanceRepository;

    private final MemberValidateAccessPermission memberValidateAccessPermission;

    public void createAttendance(final Long memberId) {
        final Member member = memberValidateAccessPermission.getValidateExistMember(memberId);

        if (isMemberAlreadyAttend(member.getId())) {
            throw new AttendanceException(ALREADY_ATTENDED);
        }
        attendanceRepository.save(Attendance.builder()
                .memberId(memberId)
                .build());
    }

    private boolean isMemberAlreadyAttend(final Long memberId) {
        return attendanceRepository.existsByMemberIdAndDate(memberId, LocalDate.now());
    }
}
