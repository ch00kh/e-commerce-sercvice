package kr.hhplus.be.server.global.util;

import kr.hhplus.be.server.global.aop.DistributedLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CustomSpelExpressionParser {

    private static final SpelExpressionParser PARSER = new SpelExpressionParser();

    public static List<String> parseKey(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        DistributedLock distributedLock = signature.getMethod().getAnnotation(DistributedLock.class);
        String value = distributedLock.value();

        StandardEvaluationContext context = new StandardEvaluationContext();

        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        return !value.contains("[*]") ? parseSingleKey(value, context) : parseMultiKey(value, context);
    }


    private static List<String> parseSingleKey(String value, StandardEvaluationContext context) {
        Expression expression = PARSER.parseExpression(value, ParserContext.TEMPLATE_EXPRESSION);
        String lockKey = expression.getValue(context, String.class);
        return lockKey != null ? Collections.singletonList(lockKey) : Collections.emptyList();
    }

    private static List<String> parseMultiKey(String value, StandardEvaluationContext context) {
        String prefix = value.substring(0, value.indexOf("#{"));
        String spel = value.substring(value.indexOf("#{") + 2, value.indexOf("}"));
        String listOfDto = spel.substring(0, spel.indexOf("[*]"));
        String field = spel.substring(spel.lastIndexOf(".") + 1);

        Expression listExpression = PARSER.parseExpression(listOfDto);
        List<?> expressionValue = listExpression.getValue(context, List.class);

        return expressionValue.stream()
                .map(list -> {
                    try {
                        Method getter = list.getClass().getMethod(field);
                        Object id = getter.invoke(list);
                        return prefix + id;

                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }
}
