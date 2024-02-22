package doore.member.domain;

import lombok.Getter;

@Getter
public enum StudyRoleType {
    ROLE_스터디장(1),
    ROLE_스터디원(2),
    ;

    private final long id;

    StudyRoleType(long id) {
        this.id = id;
    }
}
