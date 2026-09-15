package com.lendmesh.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SecurityException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String SECRET = "test-secret-test-secret-test-secret-test-secret";

    private final JwtService jwtService = new JwtService(SECRET, 60);

    @Test
    void aTokenParsesBackToTheSubjectAndRolesItWasIssuedWith() {
        String token = jwtService.issue("user-1", List.of("LENDER", "BORROWER"));

        Claims claims = jwtService.parse(token);

        assertThat(claims.getSubject()).isEqualTo("user-1");
        assertThat(claims.get("roles", List.class)).containsExactly("LENDER", "BORROWER");
    }

    @Test
    void aTokenSignedWithADifferentSecretIsRejected() {
        JwtService other = new JwtService("different-secret-different-secret-different-y", 60);
        String token = other.issue("user-1", List.of("BORROWER"));

        assertThatThrownBy(() -> jwtService.parse(token)).isInstanceOf(SecurityException.class);
    }

    @Test
    void anAlreadyExpiredTokenIsRejected() throws InterruptedException {
        JwtService instantlyExpiring = new JwtService(SECRET, 0);
        String token = instantlyExpiring.issue("user-1", List.of("BORROWER"));
        Thread.sleep(5);

        assertThatThrownBy(() -> instantlyExpiring.parse(token)).isInstanceOf(ExpiredJwtException.class);
    }
}
