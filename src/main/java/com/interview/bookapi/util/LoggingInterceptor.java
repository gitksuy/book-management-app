package com.interview.bookapi.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Log the request
        if (request instanceof ContentCachingRequestWrapper) {
            logRequest(request);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // Log the response
        if (response instanceof ContentCachingResponseWrapper responseWrapper) {
            logResponse(request, responseWrapper);
            try {
                // Important: copy content to the original response
                responseWrapper.copyBodyToResponse();
            } catch (IOException e) {
                logger.error("Failed to copy response body", e);
            }
        }
    }

    private void logRequest(HttpServletRequest request) {
        Map<String, String> headers = getRequestHeaders(request);
        String queryString = request.getQueryString() == null ? "" : "?" + request.getQueryString();
        String requestBody = getRequestBody(request);

        logger.info("REQUEST: {} {}{}", request.getMethod(), request.getRequestURI(), queryString);
        logger.info("REQUEST HEADERS: {}", headers);
        if (!requestBody.isEmpty()) {
            logger.info("REQUEST BODY: {}", requestBody);
        }
    }

    private void logResponse(HttpServletRequest request, ContentCachingResponseWrapper response) {
        String queryString = request.getQueryString() == null ? "" : "?" + request.getQueryString();
        String responseBody = getResponseBody(response);

        logger.info("RESPONSE: {} {}{} - STATUS: {}", request.getMethod(), request.getRequestURI(), queryString, response.getStatus());
        logger.info("RESPONSE HEADERS: {}", getResponseHeaders(response));
        if (!responseBody.isEmpty()) {
            logger.info("RESPONSE BODY: {}", responseBody);
        }
    }

    private String getRequestBody(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper wrapper) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                try {
                    return new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
                } catch (UnsupportedEncodingException e) {
                    return "[Body content cannot be decoded]";
                }
            }
        }
        return "";
    }

    private Map<String, String> getRequestHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        return headers;
    }

    private String getResponseBody(ContentCachingResponseWrapper response) {
        byte[] buf = response.getContentAsByteArray();
        if (buf.length > 0) {
            try {
                return new String(buf, 0, buf.length, response.getCharacterEncoding());
            } catch (UnsupportedEncodingException e) {
                return "[Body content cannot be decoded]";
            }
        }
        return "";
    }

    private Map<String, String> getResponseHeaders(HttpServletResponse response) {
        Map<String, String> headers = new HashMap<>();
        for (String headerName : response.getHeaderNames()) {
            headers.put(headerName, response.getHeader(headerName));
        }
        return headers;
    }
}