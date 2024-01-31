package doore.study.domain;

import doore.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ParticipantCurriculumItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean isChecked;

    @Column(nullable = false)
    private Boolean isDeleted;

    @Column(nullable = false)
    private Long participantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curriculum_item_id", nullable = false)
    private CurriculumItem curriculumItem;

    @Builder
    public ParticipantCurriculumItem(Boolean isChecked, Boolean isDeleted, Long participantId,
                                     CurriculumItem curriculumItem) {
        this.isChecked = isChecked;
        this.isDeleted = isDeleted;
        this.participantId = participantId;
        this.curriculumItem = curriculumItem;
    }

    public void complete() {
        this.isChecked = true;
    }

    public void incomplete() {
        this.isChecked = false;
    }
}
