package doore.member.domain.repository;

import doore.member.domain.MemberTeam;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberTeamRepository extends JpaRepository<MemberTeam, Long> {
	List<MemberTeam> findAllByTeamId(final Long teamId);

	Long countByTeamId(final Long teamId);

	@Query("select mt from MemberTeam mt "
		+ "where mt.teamId = :teamId "
		+ "and (mt.member.name like :keyword% or mt.member.email like :keyword%)")
	List<MemberTeam> findAllByTeamIdAndKeyword(final Long teamId, final String keyword);

	void deleteByTeamIdAndMemberId(final Long teamId, final Long memberId);

	Boolean existsByTeamIdAndMemberId(final Long teamId, final Long memberId);
}
