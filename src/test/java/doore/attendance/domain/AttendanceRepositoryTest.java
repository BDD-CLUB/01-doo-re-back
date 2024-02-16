package doore.attendance.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import doore.attendance.domain.Attendance;
import doore.attendance.domain.repository.AttendanceRepository;
import doore.config.JpaAuditingConfig;
import doore.helper.RepositorySliceTest;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import(JpaAuditingConfig.class)
public class AttendanceRepositoryTest extends RepositorySliceTest {
    @Autowired
    AttendanceRepository attendanceRepository;

    @Test
    @DisplayName("[성공] 해당 날짜에 회원의 출석이 존재하는지 확인한다.")
    public void existsByMemberIdAndDate_해당_날짜에_회원의_출석이_존재하는지_확인한다_성공() {
        assertFalse(attendanceRepository.existsByMemberIdAndDate(1L, LocalDate.now()));

        Attendance attendance = Attendance.builder()
                .memberId(1L)
                .build();

        attendanceRepository.save(attendance);

        assertTrue(attendanceRepository.existsByMemberIdAndDate(1L,LocalDate.now()));
    }
}
