package doore.study.persistence;

import doore.study.persistence.dto.StudyInformation;
import doore.study.persistence.dto.StudyOverview;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StudyDao {
    private static final RowMapper<StudyOverview> ROW_MAPPER = (rs, rowNum) ->
            new StudyOverview(
                    new StudyInformation(
                            rs.getLong("study.id"),
                            rs.getString("study.name"),
                            rs.getString("study.description"),
                            rs.getDate("start_date"),
                            rs.getDate("end_date"),
                            rs.getString("status"),
                            rs.getBoolean("study.is_deleted"),
                            rs.getLong("team.id"),
                            rs.getString("team.name"),
                            rs.getString("team.description"),
                            rs.getString("team.image_url"),
                            rs.getBoolean("team.is_deleted"),
                            rs.getLong("crop.id"),
                            rs.getString("crop.name"),
                            rs.getString("crop.image_url")
                    ),
                    rs.getLong("curriculum_item.id"),
                    rs.getString("curriculum_item.name"),
                    rs.getInt("item_order"),
                    rs.getBoolean("curriculum_item.is_deleted")

            );

    private final JdbcTemplate jdbctemplate;

    public List<StudyOverview> findMyStudy(final Long memberId) {
        StringBuilder sqlBuilder = new StringBuilder();

        sqlBuilder
                .append("select * ")
                .append("from study ")
                .append("    join participant on study.id = participant.study_id ")
                .append("         join team on study.team_id = team.id ")
                .append("         join crop on study.crop_id = crop.id ")
                .append("         left outer join curriculum_item on study.id = curriculum_item.study_id ")
                .append("where participant.member_id=?");

        return jdbctemplate.query(sqlBuilder.toString(), ROW_MAPPER, memberId);
    }
}
