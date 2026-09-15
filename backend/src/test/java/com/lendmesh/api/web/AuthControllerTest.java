package com.lendmesh.api.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lendmesh.api.domain.UserRole;
import com.lendmesh.api.security.JwtService;
import com.lendmesh.api.service.AuthService;
import com.lendmesh.api.web.dto.AuthDtos.LoginRequest;
import com.lendmesh.api.web.dto.AuthDtos.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @Test
    void registeringWithAMalformedEmailIsRejected() throws Exception {
        RegisterRequest request = new RegisterRequest("New User", "not-an-email", "password123", Set.of(UserRole.BORROWER));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registeringWithNoRolesIsRejected() throws Exception {
        RegisterRequest request = new RegisterRequest("New User", "new@example.com", "password123", Set.of());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registeringWithValidDataReturnsAToken() throws Exception {
        when(authService.register("New User", "new@example.com", "password123", Set.of(UserRole.BORROWER)))
                .thenReturn("a.jwt.token");
        RegisterRequest request = new RegisterRequest("New User", "new@example.com", "password123", Set.of(UserRole.BORROWER));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("a.jwt.token"));
    }

    @Test
    void loggingInWithWrongCredentialsMapsTo401() throws Exception {
        when(authService.login(any(), any())).thenThrow(new BadCredentialsException("Invalid email or password"));
        LoginRequest request = new LoginRequest("desk@example.com", "wrong-password");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
