package dev.felix2000jp.springapplicationtemplate.shared.web;

import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
class TraceIdHeaderFilter extends OncePerRequestFilter {

    private final Tracer tracer;

    TraceIdHeaderFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        var currentSpan = tracer.currentSpan();

        if (currentSpan != null) {
            response.setHeader("X-Trace-Id", currentSpan.context().traceId());
        }

        filterChain.doFilter(request, response);
    }
}
