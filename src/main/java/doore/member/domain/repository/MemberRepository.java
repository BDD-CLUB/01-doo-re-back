package doore.member.domain.repository;

import doore.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByGoogleId(final String googleId);

    @Query(value = "SELECT name FROM member WHERE id = :id", nativeQuery = true)
    Optional<String> findNameByIdNative(@Param("id") final Long memberId);
}
