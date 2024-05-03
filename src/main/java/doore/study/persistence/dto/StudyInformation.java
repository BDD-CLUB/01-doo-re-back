package doore.study.persistence.dto;


import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class StudyInformation {
    Long id;
    String name;
    String description;
    Date startDate;
    Date endDate;
    String status;
    Boolean isDeleted;
    Long teamId;
    String teamName;
    String teamDescription;
    String teamImageUrl;
    Boolean teamIsDeleted;
    Long cropId;
    String cropName;
    String cropImageUrl;
}
