package doore.member.domain;

import static doore.member.domain.TeamRoleType.ROLE_팀장;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamRole {

    private static final int MAX_ROLE_NAME_LENGTH = 45;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long teamId;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "role", nullable = false, length = MAX_ROLE_NAME_LENGTH)
    private TeamRoleType teamRoleType;

    @Column(nullable = false)
    private Long memberId;

    @Builder
    private TeamRole(Long teamId, TeamRoleType teamRoleType, Long memberId) {
        this.teamId = teamId;
        this.teamRoleType = teamRoleType;
        this.memberId = memberId;
    }

    public void updateTeamMaster() {
        this.teamRoleType = ROLE_팀장;
    }

}
