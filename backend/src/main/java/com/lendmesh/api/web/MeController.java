package com.lendmesh.api.web;

import com.lendmesh.api.domain.AppUser;
import com.lendmesh.api.service.AuthService;
import com.lendmesh.api.service.WalletService;
import com.lendmesh.api.web.dto.MeResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MeController {

    private final AuthService authService;
    private final WalletService walletService;

    public MeController(AuthService authService, WalletService walletService) {
        this.authService = authService;
        this.walletService = walletService;
    }

    @GetMapping("/api/me")
    public MeResponse me(Authentication authentication) {
        AppUser user = authService.requireById(authentication.getName());
        var wallet = walletService.requireWallet(user.getId());
        return new MeResponse(user.getId(), user.getDisplayName(), user.getEmail(), user.getRoles(), wallet.getBalance());
    }
}
