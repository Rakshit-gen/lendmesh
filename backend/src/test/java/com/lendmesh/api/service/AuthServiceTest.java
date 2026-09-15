package com.lendmesh.api.service;

import com.lendmesh.api.domain.AppUser;
import com.lendmesh.api.domain.UserRole;
import com.lendmesh.api.exception.ConflictException;
import com.lendmesh.api.repository.AppUserRepository;
import com.lendmesh.api.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private AppUserRepository appUserRepository;
    private WalletService walletService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private AuthService authService;

    @BeforeEach
    void setUp() {
        appUserRepository = mock(AppUserRepository.class);
        walletService = mock(WalletService.class);
        JwtService jwtService = new JwtService("test-secret-test-secret-test-secret-test-secret", 60);
        authService = new AuthService(appUserRepository, passwordEncoder, jwtService, walletService);
    }

    @Test
    void registeringWithAnAlreadyUsedEmailIsRejected() {
        when(appUserRepository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register("Someone", "taken@example.com", "password123", Set.of(UserRole.BORROWER)))
                .isInstanceOf(ConflictException.class);
        verifyNoInteractions(walletService);
    }

    @Test
    void registeringOpensAWalletWithTheStandardStartingBalanceAndReturnsAToken() {
        AppUser saved = new AppUser("New User", "new@example.com", "hash", Set.of(UserRole.BORROWER));
        when(appUserRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(appUserRepository.save(any())).thenReturn(saved);

        String token = authService.register("New User", "new@example.com", "password123", Set.of(UserRole.BORROWER));

        assertThat(token).isNotBlank();
        verify(walletService).openWallet(any(), eq(new BigDecimal("10000.00")));
    }

    @Test
    void loggingInWithAWrongPasswordIsRejected() {
        AppUser user = new AppUser("Desk", "desk@example.com", passwordEncoder.encode("correct-password"), Set.of(UserRole.LENDER));
        when(appUserRepository.findByEmail("desk@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.login("desk@example.com", "wrong-password"))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void loggingInWithAnUnknownEmailIsRejected() {
        when(appUserRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login("nobody@example.com", "anything"))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void loggingInWithCorrectCredentialsIssuesAToken() {
        AppUser user = new AppUser("Desk", "desk@example.com", passwordEncoder.encode("correct-password"), Set.of(UserRole.LENDER));
        when(appUserRepository.findByEmail("desk@example.com")).thenReturn(Optional.of(user));

        String token = authService.login("desk@example.com", "correct-password");

        assertThat(token).isNotBlank();
    }
}
