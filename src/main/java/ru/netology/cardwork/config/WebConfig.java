package ru.netology.cardwork.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import ru.netology.cardwork.dto.Transfer;
import ru.netology.cardwork.model.TransferAmount;

import java.util.List;
import java.util.Objects;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Add resolvers to support custom controller method argument types.
     * <p>This does not override the built-in support for resolving handler
     * method arguments. To customize the built-in support for argument
     * resolution, configure {@link RequestMappingHandlerAdapter} directly.
     *
     * @param resolvers initially an empty list
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new TransferResolver());
    }


    private static final class TransferResolver implements HandlerMethodArgumentResolver {
        /**
         * Whether the given {@linkplain MethodParameter method parameter} is
         * supported by this resolver.
         *
         * @param parameter the method parameter to check
         * @return {@code true} if this resolver supports the supplied parameter;
         * {@code false} otherwise
         */
        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return parameter.getParameterType().isAssignableFrom(Transfer.class);
        }

        /**
         * Resolves a method parameter into an argument value from a given request.
         * A {@link ModelAndViewContainer} provides access to the model for the
         * request. A {@link WebDataBinderFactory} provides a way to create
         * a {@link WebDataBinder} instance when needed for data binding and
         * type conversion purposes.
         *
         * @param parameter     the method parameter to resolve. This parameter must
         *                      have previously been passed to {@link #supportsParameter} which must
         *                      have returned {@code true}.
         * @param mavContainer  the ModelAndViewContainer for the current request
         * @param webRequest    the current request
         * @param binderFactory a factory for creating {@link WebDataBinder} instances
         * @return the resolved argument value, or {@code null} if not resolvable
         * @throws Exception in case of errors with the preparation of argument values
         */
        @Override
        public Object resolveArgument(MethodParameter parameter,
                                      ModelAndViewContainer mavContainer,
                                      NativeWebRequest webRequest,
                                      WebDataBinderFactory binderFactory) throws Exception {

            System.out.println("ArgumentResolver runs");    // monitor

            String cardFromNumber = webRequest.getParameter("cardFromNumber");
            String cardFromValidTill = webRequest.getParameter("cardFromValidTill");
            String cardFromCVV = webRequest.getParameter("cardFromCVV");
            String cardToNumber = webRequest.getParameter("cardToNumber");
            int value = Integer.parseInt(Objects.requireNonNull(webRequest.getParameter("value")));
            String currency = webRequest.getParameter("currency");
            return new Transfer(cardFromNumber, cardFromValidTill,
                    cardFromCVV,
                    cardToNumber,
                    new TransferAmount(value, currency));
        }
    }
}
