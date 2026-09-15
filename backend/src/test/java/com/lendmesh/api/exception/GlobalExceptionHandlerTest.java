package com.lendmesh.api.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void badCredentialsMapsTo401() {
        ResponseEntity<ApiError> response = handler.handleBadCredentials(new BadCredentialsException("nope"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().message()).isEqualTo("nope");
    }

    @Test
    void notFoundMapsTo404() {
        ResponseEntity<ApiError> response = handler.handleNotFound(new NotFoundException("Listing not found"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().message()).isEqualTo("Listing not found");
    }

    @Test
    void conflictMapsTo409() {
        ResponseEntity<ApiError> response = handler.handleConflict(new ConflictException("Email already in use"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void illegalArgumentAndIllegalStateBothMapTo400() {
        ResponseEntity<ApiError> fromArgument = handler.handleBadRequest(new IllegalArgumentException("Bad amount"));
        ResponseEntity<ApiError> fromState = handler.handleBadRequest(new IllegalStateException("Already funded"));

        assertThat(fromArgument.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(fromState.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
