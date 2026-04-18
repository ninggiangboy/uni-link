package dev.ngb.infrastructure.web;

import dev.ngb.domain.DomainException;
import dev.ngb.web.ErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    @ApiResponse(
            responseCode = "4xx",
            description = "Domain error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException ex) {
        return ResourceResponse.domainError(ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleInternal(Exception ex) {

        log.error("Internal server error", ex);

        var body = ErrorResponse.of(
                "INTERNAL_ERROR",
                "Unexpected error occurred"
        );

        return ResourceResponse.serverError(body);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex) {
        var body = ErrorResponse.of(
                "MISSING_PARAM",
                "Missing request parameter: " + ex.getParameterName()
        );
        return ResourceResponse.badRequest(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMissingBody(HttpMessageNotReadableException ex) {
        var body = ErrorResponse.of(
                "INVALID_BODY",
                "Request body is missing or malformed"
        );
        return ResourceResponse.badRequest(body);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        var body = ErrorResponse.of(
                "TYPE_MISMATCH",
                "Invalid value for parameter: " + ex.getName()
        );
        return ResourceResponse.badRequest(body);
    }
}
