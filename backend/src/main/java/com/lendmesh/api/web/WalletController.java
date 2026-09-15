package com.lendmesh.api.web;

import com.lendmesh.api.domain.LedgerEntry;
import com.lendmesh.api.service.WalletService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/api/wallet")
    public WalletResponse wallet(Authentication authentication) {
        var wallet = walletService.requireWallet(authentication.getName());
        return new WalletResponse(wallet.getBalance());
    }

    @GetMapping("/api/wallet/history")
    public List<LedgerEntry> history(Authentication authentication) {
        return walletService.history(authentication.getName());
    }

    public record WalletResponse(BigDecimal balance) {
    }
}
