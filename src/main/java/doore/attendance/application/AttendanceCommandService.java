package doore.attendance.application;

import static doore.attendance.exception.AttendanceExceptionType.ALREADY_ATTENDED;
import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;

import doore.attendance.domain.Attendance;
import doore.attendance.domain.repository.AttendanceRepository;
import doore.attendance.exception.AttendanceException;
import doore.member.domain.Member;
import doore.member.domain.repository.MemberRepository;
import doore.member.exception.MemberException;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AttendanceCommandService {

    private final MemberRepository memberRepository;
    private final AttendanceRepository attendanceRepository;

    public void createAttendance(final Long memberId) {
        final Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));

        if (isMemberAlreadyAttend(member.getId())) {
            throw new AttendanceException(ALREADY_ATTENDED);
        }
        final Attendance attendance = Attendance.builder()
                .memberId(memberId)
                .build();
        attendanceRepository.save(attendance);
    }

    private boolean isMemberAlreadyAttend(final Long memberId) {
        return attendanceRepository.existsByMemberIdAndDate(memberId, LocalDate.now());
    }
}
