package doore.restdocs.docs;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import doore.restdocs.RestDocsTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FileDocsTest extends RestDocsTest {

    @Test
    @DisplayName("[성공] 이미지 파일의 URL을 조회한다.")
    public void 이미지_파일_URL_조회() throws Exception {
        String uuid = "8a463aa4-b1dc-4f27-9c3f-53b94dc45e74.jpg";
        String s3Url = "https://s3.amazonaws.com/my-bucket/images/" + uuid;
        when(s3ImageFileService.getS3Url(uuid)).thenReturn(s3Url);

        mockMvc.perform(get("/files/images/{uuid}", uuid))
            .andExpect(status().isMovedPermanently())
            .andDo(document("file-get-image-url",
                pathParameters(
                    parameterWithName("uuid").description("이미지 파일의 UUID")
                ),
                responseHeaders(
                    headerWithName("Location").description("S3에서 제공되는 Public URL")
                )
            ));
    }

    @Test
    @DisplayName("[성공] 문서 파일의 Presigned URL을 생성한다.")
    public void 문서_파일_Presigned_URL_생성() throws Exception {
        String uuid = "8a463aa4-b1dc-4f27-9c3f-53b94dc45e74.pdf";
        String presignedUrl = "https://s3.amazonaws.com/my-bucket/documents/" + uuid + "?signature=123";
        when(s3DocumentFileService.generatePresignedUrl(uuid)).thenReturn(presignedUrl);

        mockMvc.perform(get("/files/documents/{uuid}", uuid))
            .andExpect(status().isFound())
            .andDo(document("file-generate-presigned-url",
                pathParameters(
                    parameterWithName("uuid").description("Presigned URL을 생성할 문서의 UUID")
                ),
                responseHeaders(
                    headerWithName("Location").description("S3에서 제공되는 Presigned URL")
                )
            ));
    }
}
