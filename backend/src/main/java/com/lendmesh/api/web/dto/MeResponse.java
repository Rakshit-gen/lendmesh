package com.lendmesh.api.web.dto;

import com.lendmesh.api.domain.UserRole;

import java.math.BigDecimal;
import java.util.Set;

public record MeResponse(String userId, String displayName, String email, Set<UserRole> roles, BigDecimal walletBalance) {
}
