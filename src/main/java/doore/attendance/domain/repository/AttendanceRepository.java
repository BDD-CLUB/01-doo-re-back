package doore.attendance.domain.repository;

import doore.attendance.domain.Attendance;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    @Query(value = "select count(*)>0 from Attendance a where a.memberId= :memberId and DATE(a.createdAt)=:date")
    Boolean existsByMemberIdAndDate(@Param("memberId")Long memberId, @Param("date")LocalDate date);
}
