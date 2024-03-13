package doore.restdocs;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import doore.document.application.DocumentCommandService;
import doore.document.application.DocumentQueryService;
import doore.attendance.application.AttendanceCommandService;
import doore.helper.ApiTestHelper;
import doore.login.application.LoginService;
import doore.member.application.MemberCommandService;
import doore.member.application.MemberTeamQueryService;
import doore.study.application.CurriculumItemCommandService;
import doore.study.application.ParticipantCommandService;
import doore.study.application.ParticipantQueryService;
import doore.study.application.StudyCommandService;
import doore.study.application.StudyQueryService;
import doore.team.application.TeamCommandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@Import(RestDocsConfiguration.class)
@ExtendWith(RestDocumentationExtension.class)
public abstract class RestDocsTest extends ApiTestHelper {

    @MockBean
    protected LoginService loginService;

    @MockBean
    protected MemberCommandService memberCommandService;

    @MockBean
    protected MemberTeamQueryService memberTeamQueryService;

    @MockBean
    protected TeamCommandService teamCommandService;

    @MockBean
    protected StudyCommandService studyCommandService;

    @MockBean
    protected StudyQueryService studyQueryService;

    @MockBean
    protected ParticipantCommandService participantCommandService;

    @MockBean
    protected ParticipantQueryService participantQueryService;

    @MockBean
    protected AttendanceCommandService attendanceCommandService;

    @MockBean
    protected CurriculumItemCommandService curriculumItemCommandService;

    @MockBean
    protected DocumentQueryService documentQueryService;

    @MockBean
    protected DocumentCommandService documentCommandService;

    @Autowired
    protected RestDocumentationResultHandler restDocs;

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
}
