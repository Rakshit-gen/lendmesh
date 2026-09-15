package com.lendmesh.api.service;

import com.lendmesh.api.domain.AppUser;
import com.lendmesh.api.domain.UserRole;
import com.lendmesh.api.exception.ConflictException;
import com.lendmesh.api.exception.NotFoundException;
import com.lendmesh.api.repository.AppUserRepository;
import com.lendmesh.api.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;

@Service
public class AuthService {

    private static final BigDecimal STARTING_BALANCE = new BigDecimal("10000.00");

    private final AppUserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final WalletService walletService;

    public AuthService(AppUserRepository users, PasswordEncoder passwordEncoder,
                        JwtService jwtService, WalletService walletService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.walletService = walletService;
    }

    @Transactional
    public String register(String displayName, String email, String rawPassword, Set<UserRole> roles) {
        if (users.existsByEmail(email)) {
            throw new ConflictException("An account with this email already exists");
        }
        AppUser user = users.save(new AppUser(displayName, email, passwordEncoder.encode(rawPassword), roles));
        walletService.openWallet(user.getId(), STARTING_BALANCE);
        return jwtService.issue(user.getId(), roles.stream().map(Enum::name).toList());
    }

    public String login(String email, String rawPassword) {
        AppUser user = users.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        return jwtService.issue(user.getId(), user.getRoles().stream().map(Enum::name).toList());
    }

    public AppUser requireById(String userId) {
        return users.findById(userId).orElseThrow(() -> new NotFoundException("No such user"));
    }
}
