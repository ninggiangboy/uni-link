package dev.ngb.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class DomainException extends RuntimeException {
    private final DomainError error;
    private Map<String, Object> details;
}
