package doore.member.domain;

import doore.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String googleId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private Boolean isDeleted;

    public Member(final Long id, final String name, final String googleId, final String email, final String imageUrl) {
        this.id = id;
        this.name = name;
        this.googleId = googleId;
        this.email = email;
        this.imageUrl = imageUrl;
        this.isDeleted = false;
    }

    public Member(final String name, final String googleId, final String email, final String imageUrl) {
        this(null, name, googleId, email, imageUrl);
    }
}
