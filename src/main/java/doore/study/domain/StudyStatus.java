package doore.study.domain;

import lombok.Getter;

@Getter
public enum StudyStatus {
    IN_PROGRESS(1),
    UPCOMING(2),
    ENDED(3)
    ;

    private final int order;

    StudyStatus(int order) {
        this.order = order;
    }
}
