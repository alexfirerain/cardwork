package ru.netology.cardwork.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.netology.cardwork.dto.Transfer;
import ru.netology.cardwork.model.Card;
import ru.netology.cardwork.model.TransferAmount;

import java.util.List;
import java.util.Objects;

/**
 * Конфигуратор специфических настроек приложения.
 * В данной реализации содержит конвертер пары параметров "user + password"
 * в юзер-объект.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new TransferResolver());
    }

    /**
     * Преобразователь запроса, возвращающий из полученных строк имени и пароля единый юзер-объект.
     */
    private static final class TransferResolver implements HandlerMethodArgumentResolver {
        /**
         * Поддерживается ли этим распознавателем данный {@linkplain MethodParameter параметр метода}.
         *
         * @param parameter проверяемый параметр метода.
         * @return {@code истинно} если этот распознаватель поддерживает предложенный параметр;
         * {@code ложно} в ином случае.
         */
        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return parameter.getParameterType().isAssignableFrom(Transfer.class);
        }



        /**
         * Разрешает параметр метода в значение аргумента из данного запроса.
         * {@link ModelAndViewContainer} предоставляет запросу доступ к модели.
         * {@link WebDataBinderFactory} предоставляет возможность создать экземпляр
         * {@link WebDataBinder}, когда это требуется для целей связывания данных
         * или преобразования типа.
         *
         * @param parameter     разрешаемый параметр метода. Этот параметр должен
         *                      быть предварительно передан в {@link #supportsParameter}, который
         *                      должен вернуть {@code истинно}.
         * @param mavContainer  ModelAndViewContainer текущего запроса.
         * @param webRequest    текущий запрос.
         * @param binderFactory фабрика для производства экземпляров {@link WebDataBinder}.
         * @return разрешённое значение аргумента, либо {@code ничто}, если оно не разрешимо.
         * @throws Exception в случае ошибок с подготовкой значений аргумента.
         */
        @Override
        public Object resolveArgument(MethodParameter parameter,
                                      ModelAndViewContainer mavContainer,
                                      NativeWebRequest webRequest,
                                      WebDataBinderFactory binderFactory) throws Exception {
            String number = webRequest.getParameter("user");
            String validTill = webRequest.getParameter("password");
            String cardCVV = webRequest.getParameter("password");
            String targetCard = webRequest.getParameter("cardToNumber");

            Integer value = Integer.valueOf(Objects.requireNonNull(webRequest.getParameter("value")));
            String currency = webRequest.getParameter("currency");
            return new Transfer(new Card(number, validTill, cardCVV), targetCard, new TransferAmount(value, currency));
        }
    }

}
