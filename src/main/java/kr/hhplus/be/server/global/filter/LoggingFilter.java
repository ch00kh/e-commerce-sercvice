package kr.hhplus.be.server.global.filter;


import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static java.util.Collections.enumeration;

@Slf4j
public class LoggingFilter implements Filter {

    private static final String NO_CONTENT = "No Content";

    @Override
    public void init(FilterConfig filterConfig) {
        log.info("Logging Filter Init");
    }

    @Override
    public void destroy() {
        log.info("Logging Filter Destroy");
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper((HttpServletRequest) request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper((HttpServletResponse) response);

        try {
            chain.doFilter(wrappedRequest, wrappedResponse);

        } finally {
            loggingRequest(wrappedRequest);

            byte[] responseBody = wrappedResponse.getContentAsByteArray();
            wrappedResponse.copyBodyToResponse();

            loggingResponse(wrappedResponse, responseBody);
        }
    }

    private static void loggingRequest(ContentCachingRequestWrapper request) {
        log.info("======== Request Start ========");
        log.info("Request URI : {}", request.getRequestURI());
        log.info("Request Method : {}", request.getMethod());
        log.info("Request Headers : {}", getRequestHeaders(request));
        log.info("Request Parameters : {}", getParameters(request));
        log.info("Request Body : {}", getRequestBody(request));
        log.info("Request Content Type : {}", request.getContentType());
        log.info("======== Request End ========");
    }

    private static void loggingResponse(ContentCachingResponseWrapper response, byte[] responseBody) {
        log.info("======== Response Start ========");
        log.info("Response Status : {}", response.getStatus());
        log.info("Response Headers : {}", getResponseHeaders(response));
        log.info("Response Body : {}", getResponseBody(responseBody));
        log.info("======== Response End ========");
    }

    private static String getRequestHeaders(HttpServletRequest request) {
        return getHeadersAsString(request.getHeaderNames(), request::getHeader);
    }

    private static String getResponseHeaders(HttpServletResponse response) {
        return getHeadersAsString(enumeration(response.getHeaderNames()), name -> String.join(", ", response.getHeaders(name)));
    }

    private static String getHeadersAsString(Enumeration<String> headerNames, UnaryOperator<String> headerResolver) {
        StringBuilder headers = new StringBuilder();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            headers.append(name).append(": ").append(headerResolver.apply(name)).append(", ");
        }
        if (headers.length() > 2) {
            headers.setLength(headers.length() - 2);
        }
        return headers.toString();
    }

    private static String getParameters(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap.isEmpty()) {
            return NO_CONTENT;
        }
        return parameterMap.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + String.join(",", entry.getValue()))
                .collect(Collectors.joining(", "));
    }

    private static String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] requestBody = request.getContentAsByteArray();
        if (requestBody == null || requestBody.length == 0) {
            return NO_CONTENT;
        }
        return new String(requestBody, StandardCharsets.UTF_8);
    }

    private static String getResponseBody(byte[] responseBody) {
        if (responseBody == null || responseBody.length == 0) {
            return NO_CONTENT;
        }
        return new String(responseBody, StandardCharsets.UTF_8);
    }

}
