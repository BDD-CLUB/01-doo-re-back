package doore.attendance.application;

import doore.attendance.domain.Attendance;
import doore.member.domain.Member;
import doore.attendance.domain.repository.AttendanceRepository;
import doore.member.domain.repository.MemberRepository;
import doore.attendance.exception.AttendanceException;
import doore.attendance.exception.AttendanceExceptionType;
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

    public void createAttendance(Long memberId) {
        if (memberId == null) {
            throw new IllegalArgumentException(); //todo: MemberException(EMPTY_AUTHORIZATION); 이후 권한 처리
        }
        Member member = memberRepository.findById(memberId)
                .orElseThrow(IllegalArgumentException::new); //todo: new MemberException(NOT_FOUND_MEMBER)

        if (isMemberAlreadyAttend(member.getId())) {
            throw new AttendanceException(AttendanceExceptionType.ALREADY_ATTENDED);
        }
        Attendance attendance = Attendance.builder()
                .memberId(memberId)
                .build();
        attendanceRepository.save(attendance);
    }

    private boolean isMemberAlreadyAttend(Long memberId) {
        return attendanceRepository.existsByMemberIdAndDate(memberId, LocalDate.now());
    }


}
