package doore.crop;

import doore.crop.domain.Crop;
import doore.crop.domain.repository.CropRepository;
import doore.study.StudyFixture;
import doore.study.domain.CurriculumItem;
import doore.study.domain.Study;
import doore.study.domain.StudyStatus;
import doore.team.TeamFixture;
import java.time.LocalDate;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CropFixture {
    private static CropRepository cropRepository;

    @Autowired
    public CropFixture(CropRepository cropRepository) {
        CropFixture.cropRepository = cropRepository;
    }
    public static Crop createCrop() {
        return cropRepository.save(CropFixture.rice());
    }

    public static Crop rice() {
        return Crop.builder()
                .name("벼")
                .imageUrl("https://~")
                .build();
    }
}
