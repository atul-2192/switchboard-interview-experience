package com.Switchboard.InterviewService.Filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraceFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private TraceFilter traceFilter;

    @BeforeEach
    void setUp() {
        MDC.clear();
    }

    @Test
    void doFilterInternal_WithTraceIdHeader_ShouldSetMDC() throws ServletException, IOException {
        // Arrange
        String traceId = "test-trace-id-123";
        String correlationId = "test-correlation-id-456";
        when(request.getHeader("X-Trace-Id")).thenReturn(traceId);
        when(request.getHeader("X-Correlation-Id")).thenReturn(correlationId);

        // Act
        traceFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(request, times(1)).getHeader("X-Trace-Id");
        verify(request, times(1)).getHeader("X-Correlation-Id");
    }

    @Test
    void doFilterInternal_WithoutTraceIdHeader_ShouldGenerateUUID() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("X-Trace-Id")).thenReturn(null);
        when(request.getHeader("X-Correlation-Id")).thenReturn(null);

        // Act
        traceFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(request, times(1)).getHeader("X-Trace-Id");
        verify(request, times(1)).getHeader("X-Correlation-Id");
    }

    @Test
    void doFilterInternal_WithOnlyTraceId_ShouldGenerateCorrelationId() throws ServletException, IOException {
        // Arrange
        String traceId = "test-trace-id-123";
        when(request.getHeader("X-Trace-Id")).thenReturn(traceId);
        when(request.getHeader("X-Correlation-Id")).thenReturn(null);

        // Act
        traceFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(request, times(1)).getHeader("X-Trace-Id");
        verify(request, times(1)).getHeader("X-Correlation-Id");
    }

    @Test
    void doFilterInternal_WithOnlyCorrelationId_ShouldGenerateTraceId() throws ServletException, IOException {
        // Arrange
        String correlationId = "test-correlation-id-456";
        when(request.getHeader("X-Trace-Id")).thenReturn(null);
        when(request.getHeader("X-Correlation-Id")).thenReturn(correlationId);

        // Act
        traceFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(request, times(1)).getHeader("X-Trace-Id");
        verify(request, times(1)).getHeader("X-Correlation-Id");
    }

    @Test
    void doFilterInternal_ShouldClearMDCAfterFilterChain() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("X-Trace-Id")).thenReturn("trace-123");
        when(request.getHeader("X-Correlation-Id")).thenReturn("correlation-456");

        // Act
        traceFilter.doFilterInternal(request, response, filterChain);

        // Assert - MDC should be cleared after filter execution
        assertNull(MDC.get("traceId"));
        assertNull(MDC.get("correlationId"));
    }

    @Test
    void doFilterInternal_WhenExceptionOccurs_ShouldStillClearMDC() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("X-Trace-Id")).thenReturn("trace-123");
        when(request.getHeader("X-Correlation-Id")).thenReturn("correlation-456");
        doThrow(new ServletException("Test exception")).when(filterChain).doFilter(request, response);

        // Act & Assert
        assertThrows(ServletException.class, () -> {
            traceFilter.doFilterInternal(request, response, filterChain);
        });

        // MDC should still be cleared even after exception
        assertNull(MDC.get("traceId"));
        assertNull(MDC.get("correlationId"));
    }

    @Test
    void doFilterInternal_ShouldCallFilterChain() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("X-Trace-Id")).thenReturn("trace-123");
        when(request.getHeader("X-Correlation-Id")).thenReturn("correlation-456");

        // Act
        traceFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
