package doore.study.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import doore.study.domain.StudyStatus;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
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
}
