package doore.restdocs;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.fasterxml.jackson.databind.ObjectMapper;
import doore.login.application.LoginService;
import doore.member.application.MemberCommandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@Import(RestDocsConfiguration.class)
@ExtendWith(RestDocumentationExtension.class)
public abstract class RestDocsTest {

    @MockBean
    protected LoginService loginService;

    @MockBean
    protected MemberCommandService memberCommandService;

    @Autowired
    protected RestDocumentationResultHandler restDocs;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeEach
    void setUp(
            final WebApplicationContext context,
            final RestDocumentationContextProvider restDocumentation
    ) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation).operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    protected FieldDescriptor stringFieldWithPath(final String path, final String description) {
        return fieldWithPath(path).type(JsonFieldType.STRING)
                .description(description);
    }

    protected FieldDescriptor numberFieldWithPath(final String path, final String description) {
        return fieldWithPath(path).type(JsonFieldType.NUMBER)
                .description(description);
    }

    protected FieldDescriptor booleanFieldWithPath(final String path, final String description) {
        return fieldWithPath(path).type(JsonFieldType.BOOLEAN)
                .description(description);
    }

    protected FieldDescriptor arrayFieldWithPath(final String path, final String description) {
        return fieldWithPath(path).type(JsonFieldType.ARRAY)
                .description(description);
    }

    protected ResultActions callPostApi(final String url, final Object value) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(value)));
    }

    protected ResultActions callPatchApi(final String url, final Object value) throws Exception {
        return mockMvc.perform(patch(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(value)));
    }

    protected ResultActions callPatchApi(final String url) throws Exception {
        return mockMvc.perform(patch(url)
                .contentType(MediaType.APPLICATION_JSON));
    }

    protected ResultActions callPutApi(final String url, final Object value) throws Exception {
        return mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(value)));
    }

    protected ResultActions callGetApi(final String url) throws Exception {
        return mockMvc.perform(get(url));
    }

    protected ResultActions callPostApi(final String url) throws Exception {
        return mockMvc.perform(post(url));
    }

    protected ResultActions callDeleteApi(final String url) throws Exception {
        return mockMvc.perform(delete(url));
    }

    protected ResultActions callDeleteApi(final String url, final MockHttpSession session) throws Exception {
        return mockMvc.perform(delete(url).session(session));
    }
}
