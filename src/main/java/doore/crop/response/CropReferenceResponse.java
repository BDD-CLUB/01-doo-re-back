package doore.crop.response;

import doore.crop.domain.Crop;

public record CropReferenceResponse(
        Long id,
        String name,
        String imageUrl
) {
    public static CropReferenceResponse from(final Crop crop) {
        return new CropReferenceResponse(crop.getId(), crop.getName(), crop.getImageUrl());
    }
}
