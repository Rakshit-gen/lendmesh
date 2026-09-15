package com.lendmesh.api.web;

import com.lendmesh.api.domain.LoanNote;
import com.lendmesh.api.service.MarketplaceService;
import com.lendmesh.api.web.dto.LoanDtos.FundRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loans")
public class MarketplaceController {

    private final MarketplaceService marketplaceService;

    public MarketplaceController(MarketplaceService marketplaceService) {
        this.marketplaceService = marketplaceService;
    }

    @PostMapping("/{id}/fund")
    public FundResponse fund(Authentication authentication, @PathVariable String id, @Valid @RequestBody FundRequest request) {
        LoanNote note = marketplaceService.fund(authentication.getName(), id, request.amount());
        return new FundResponse(note.getId(), note.getPrincipalCommitted());
    }

    public record FundResponse(String noteId, java.math.BigDecimal amountFunded) {
    }
}
