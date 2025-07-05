package org.api_sync.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Interceptor para capturar información adicional de requests que fallan
 * Se enfoca en timing y información complementaria
 */
@Component
@Slf4j
public class RequestErrorInterceptor implements HandlerInterceptor {

    private static final String START_TIME_ATTRIBUTE = "requestStartTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Guardar tiempo de inicio
        request.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // No hacer nada aquí - el filtro ya maneja el logging de errores
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // Solo loggear timing para requests que fallan
        if (response.getStatus() >= 400 || ex != null) {
            Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
            if (startTime != null) {
                long processingTime = System.currentTimeMillis() - startTime;
                log.error("Request processing time: {}ms for failed request: {} {} (Status: {})", 
                    processingTime, 
                    request.getMethod(), 
                    request.getRequestURI(),
                    response.getStatus());
            }
        }
    }
} 