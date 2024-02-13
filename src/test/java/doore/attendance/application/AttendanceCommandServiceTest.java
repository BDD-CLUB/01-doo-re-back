package doore.attendance.application;

import static doore.attendance.exception.AttendanceExceptionType.ALREADY_ATTENDED;
import static doore.member.MemberFixture.회원;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import doore.attendance.domain.Attendance;
import doore.attendance.domain.repository.AttendanceRepository;
import doore.attendance.exception.AttendanceException;
import doore.helper.IntegrationTest;
import doore.member.domain.Member;
import doore.member.domain.repository.MemberRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AttendanceCommandServiceTest extends IntegrationTest {

    @Autowired
    private AttendanceCommandService attendanceCommandService;
    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("[성공] 출석을 할 수 있다")
    public void createAttendance_출석을_할_수_있다_성공() {
        //given
        Member member = 회원();
        memberRepository.save(member);
        Long memberId = member.getId();

        //when
        attendanceCommandService.createAttendance(memberId);

        //then
        List<Attendance> attendances = attendanceRepository.findAll();
        assertAll(
                () -> assertThat(attendances).hasSize(1),
                () -> assertEquals(memberId, attendances.get(0).getMemberId())
        );
    }

    @Test
    @DisplayName("[실패] 회원이 없는 경우 출석 할 수 없다.")
    public void createAttendance_회원이_없는_경우_출석을_할_수_없다_성공() {
        Long notExisitMemberId = 15L;
        assertThatThrownBy(() -> attendanceCommandService.createAttendance(notExisitMemberId))
                .isInstanceOf(IllegalArgumentException.class); //todo: new MemberException(NOT_FOUND_MEMBER
    }

    @Test
    @DisplayName("[실패] 오늘 이미 출석한 경우 출석 할 수 없다.")
    public void createAttendance_오늘_이미_출석한_경우_출석을_할_수_없다_성공() {
        //given
        Member member = 회원();
        memberRepository.save(member);
        Long memberId = member.getId();

        //when
        attendanceCommandService.createAttendance(memberId);

        //then
        assertThatThrownBy(() -> attendanceCommandService.createAttendance(memberId))
                .isInstanceOf(AttendanceException.class)
                .hasMessage(ALREADY_ATTENDED.errorMessage());
    }
}
