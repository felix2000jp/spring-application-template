package dev.felix2000jp.springapplicationtemplate.shared.web;

import io.micrometer.observation.Observation.Scope;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.ServerHttpObservationFilter;

@Component
class TraceIdObservationFilter extends ServerHttpObservationFilter {

    private final Tracer tracer;

    TraceIdObservationFilter(Tracer tracer, ObservationRegistry observationRegistry) {
        super(observationRegistry);
        this.tracer = tracer;
    }

    @Override
    protected void onScopeOpened(@NonNull Scope scope, @NonNull HttpServletRequest request, @NonNull HttpServletResponse response) {
        var currentSpan = tracer.currentSpan();

        if (currentSpan != null) {
            response.setHeader("X-Trace-Id", currentSpan.context().traceId());
        }
    }

}
