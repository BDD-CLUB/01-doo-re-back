package doore.member.domain;

import lombok.Getter;

@Getter
public enum TeamRoleType {
    ROLE_팀장(1),
    ROLE_팀원(2),
    ;

    private final long id;

    TeamRoleType(long id) {
        this.id = id;
    }
}
