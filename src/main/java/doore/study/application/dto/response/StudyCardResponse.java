package doore.study.application.dto.response;

import doore.document.application.dto.response.DocumentDetailResponse;
import doore.study.application.dto.response.personalStudyResponse.PersonalStudyDetailResponse;
import java.util.List;

public record StudyCardResponse(
        PersonalStudyDetailResponse personalStudyDetailResponse,
        List<DocumentDetailResponse> documentDetailResponses
) {
}
