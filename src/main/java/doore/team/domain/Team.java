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
import org.springframework.util.Assert;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false")
@SQLDelete(sql = "UPDATE team SET is_deleted = true where id = ?")
public class Team extends BaseEntity {

    private static final int MAX_LENGTH = 250;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = MAX_LENGTH)
    private String name;

    @Column(nullable = false, length = MAX_LENGTH)
    private String description;

    @Column(nullable = false, length = MAX_LENGTH)
    private String imageUrl;

    @Column(nullable = false)
    private Boolean isDeleted;

    @Builder
    private Team(final String name, final String description, final String imageUrl, final Boolean isDeleted) {
        validateNotNull(name, description, imageUrl);
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.isDeleted = isDeleted != null ? isDeleted : false;
    }

    private void validateNotNull(final String name, final String description, final String imageUrl) {
        Assert.notNull(name, "%s는 null일 수 없습니다.".formatted(name));
        Assert.notNull(description, "%s는 null일 수 없습니다.".formatted(description));
        Assert.notNull(imageUrl, "%s는 null일 수 없습니다.".formatted(imageUrl));
    }

    public void update(final String name, final String description) {
        this.name = name;
        this.description = description;
    }

    public boolean hasImage() {
        return !imageUrl.equals("");
    }

    public void updateImageUrl(final String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
