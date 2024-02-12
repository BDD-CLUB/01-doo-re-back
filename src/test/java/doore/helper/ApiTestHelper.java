package doore.helper;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@AutoConfigureMockMvc
public abstract class ApiTestHelper {

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected MockMvc mockMvc;

    protected ResultActions callPostApi(final String url, final Object content) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(content)));
    }

    protected ResultActions callPostApi(final String url) throws Exception {
        return mockMvc.perform(post(url));
    }

    protected ResultActions callDeleteApi(final String url) throws Exception {
        return mockMvc.perform(delete(url));
    }

    protected ResultActions callGetApi(final String url) throws Exception {
        return mockMvc.perform(get(url));
    }

    protected ResultActions callPatchApi(final String url, final Object content) throws Exception {
        return mockMvc.perform(patch(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(content)));
    }

    protected ResultActions callPutApi(final String url, final Object content) throws Exception {
        return mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(content)));
    }

    protected String asJsonString(final Object content) {
        try {
            return objectMapper.writeValueAsString(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected MockPart getMockPart(final String name, final Object content) {
        return new MockPart(name, asJsonString(content).getBytes(StandardCharsets.UTF_8));
    }

    protected MockMultipartFile getMockImageFile() {
        try {
            return new MockMultipartFile(
                    "file",
                    "testImage.png",
                    "image/png",
                    new FileInputStream("src/test/resources/images/testImage.png")
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
