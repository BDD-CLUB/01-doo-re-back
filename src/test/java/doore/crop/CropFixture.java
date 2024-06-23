package doore.crop;

import doore.crop.domain.Crop;
import doore.crop.domain.repository.CropRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CropFixture {
    private static CropRepository cropRepository;

    @Autowired
    public CropFixture(final CropRepository cropRepository) {
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
