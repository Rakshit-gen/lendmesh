package com.lendmesh.api.web;

import com.lendmesh.api.config.SecurityConfig;
import com.lendmesh.api.domain.AppUser;
import com.lendmesh.api.domain.UserRole;
import com.lendmesh.api.domain.Wallet;
import com.lendmesh.api.exception.NotFoundException;
import com.lendmesh.api.security.JwtService;
import com.lendmesh.api.service.AuthService;
import com.lendmesh.api.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.math.BigDecimal;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MeController.class)
@Import(SecurityConfig.class)
class MeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private WalletService walletService;

    @MockBean
    private JwtService jwtService;

    private RequestPostProcessor asUser() {
        return user("user-1");
    }

    @Test
    void meCombinesTheProfileAndWalletBalance() throws Exception {
        AppUser appUser = new AppUser("Ada Lender", "ada@example.com", "hash", Set.of(UserRole.LENDER));
        Wallet wallet = new Wallet("user-1", new BigDecimal("750.00"));
        when(authService.requireById("user-1")).thenReturn(appUser);
        when(walletService.requireWallet(any())).thenReturn(wallet);

        mockMvc.perform(get("/api/me").with(asUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("Ada Lender"))
                .andExpect(jsonPath("$.walletBalance").value(750.00));
    }

    @Test
    void meForAnUnknownUserReturns404() throws Exception {
        when(authService.requireById("user-1")).thenThrow(new NotFoundException("No such user"));

        mockMvc.perform(get("/api/me").with(asUser()))
                .andExpect(status().isNotFound());
    }
}
