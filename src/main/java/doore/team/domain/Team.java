package doore.team.domain;

import doore.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false")
@SQLDelete(sql = "UPDATE team SET is_deleted = true where id = ?")
public class Team extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private Boolean isDeleted;

    @Builder
    private Team(final String name, final String description, final String imageUrl, final Boolean isDeleted) {
        this.name = name;
        this.description = description != null ? description : "";
        this.imageUrl = imageUrl != null ? imageUrl : "";
        this.isDeleted = isDeleted != null ? isDeleted : false;
    }

    public void update(final String name, final String description) {
        this.name = name;
        this.description = description;
    }
}
