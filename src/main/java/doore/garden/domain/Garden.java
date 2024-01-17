package doore.garden.domain;

import doore.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Garden extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate contributedDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GardenType type;

    @Column(nullable = false)
    private Boolean isDeleted;

    @Column(nullable = false)
    private Long contributionId;

    @Column(nullable = false)
    private Long teamId;

    @Column(nullable = false)
    private Long memberId;

}
