package doore.study.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import doore.study.domain.StudyStatus;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import lombok.Getter;

@Embeddable
@Getter
public class StudyResponse {
    Long id;
    String name;
    String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate endTime;
    StudyStatus status;
    Boolean isDeleted;
    Long teamId;
    Long cropId;

    public StudyResponse() {
    }

    public StudyResponse(Long id, String name, String description, LocalDate startDate, LocalDate endTime,
                         StudyStatus status, Boolean isDeleted, Long teamId, Long cropId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endTime = endTime;
        this.status = status;
        this.isDeleted = isDeleted;
        this.teamId = teamId;
        this.cropId = cropId;
    }
}
