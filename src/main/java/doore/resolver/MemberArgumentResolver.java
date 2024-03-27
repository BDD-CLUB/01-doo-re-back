package doore.resolver;

import doore.login.utils.AuthorizationExtractor;
import doore.login.utils.JwtTokenGenerator;
import doore.member.application.MemberQueryService;
import doore.member.domain.Member;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class MemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtTokenGenerator jwtTokenGenerator;
    private final MemberQueryService memberQueryService;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.getParameterType().equals(Member.class);
    }

    @Override
    public Object resolveArgument(
            final MethodParameter parameter,
            final ModelAndViewContainer modelAndViewContainer,
            final NativeWebRequest nativeWebRequest,
            final WebDataBinderFactory webDataBinderFactory
    ) {
        final HttpServletRequest request = (HttpServletRequest) nativeWebRequest.getNativeRequest();
        final String token = AuthorizationExtractor.extract(request);
        final Long memberId = Long.parseLong(jwtTokenGenerator.extractMemberId(token));

        return memberQueryService.findById(memberId);
    }

}
