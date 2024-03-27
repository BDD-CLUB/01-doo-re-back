package doore.member.domain;

import static doore.member.domain.StudyRoleType.ROLE_스터디원;
import static doore.member.domain.StudyRoleType.ROLE_스터디장;

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
public class StudyRole {

    private static final int MAX_ROLE_NAME_LENGTH = 45;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long studyId;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "role", nullable = false, length = MAX_ROLE_NAME_LENGTH)
    private StudyRoleType studyRoleType;

    @Column(nullable = false)
    private Long memberId;

    @Builder
    private StudyRole(Long studyId, StudyRoleType studyRoleType, Long memberId) {
        this.studyId = studyId;
        this.studyRoleType = studyRoleType;
        this.memberId = memberId;
    }

    public void updatePreviousStudyMaster() {
        this.studyRoleType = ROLE_스터디원;
    }

    public void updateStudyMaster() {
        this.studyRoleType = ROLE_스터디장;
    }
}
