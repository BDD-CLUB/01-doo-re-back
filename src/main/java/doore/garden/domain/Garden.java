package doore.garden.domain;

import static doore.garden.domain.GardenType.STUDY_CURRICULUM_COMPLETION;

import doore.base.BaseEntity;
import doore.document.domain.Document;
import doore.study.domain.ParticipantCurriculumItem;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Where(clause = "is_deleted = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE garden SET is_deleted = true where id = ?")
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

    @Builder
    private Garden(LocalDate contributedDate, GardenType type, Long contributionId,
                   Long teamId, Long memberId) {
        this.contributedDate = contributedDate;
        this.type = type;
        this.isDeleted = false;
        this.contributionId = contributionId;
        this.teamId = teamId;
        this.memberId = memberId;
    }

    public Garden of(Document document) {
        return Garden.builder()
                .contributedDate(LocalDate.now())
                .contributionId(document.getId())
                .memberId(document.getUploaderId())
                .teamId(document.getGroupId())
                .type(GardenType.DOCUMENT_UPLOAD)
                .build();
    }

    public Garden of(ParticipantCurriculumItem participantCurriculumItem) {
        return Garden.builder()
                .contributedDate(LocalDate.now())
                .contributionId(participantCurriculumItem.getId())
                .memberId(participantCurriculumItem.getParticipantId())
                .teamId(participantCurriculumItem.getCurriculumItem().getStudy().getTeamId())
                .type(STUDY_CURRICULUM_COMPLETION)
                .build();
    }
}
