package doore.attendance.domain.repository;

import doore.attendance.domain.Attendance;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    @Query(value = "select count(*)>0 from Attendance a where a.memberId= ?1 and DATE(a.createdAt)=?2")
    Boolean existsByMemberIdAndDate(Long memberId, LocalDate date);

}
