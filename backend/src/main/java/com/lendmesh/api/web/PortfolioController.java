package com.lendmesh.api.web;

import com.lendmesh.api.service.PortfolioService;
import com.lendmesh.api.service.PortfolioSummary;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping("/api/portfolio")
    public PortfolioSummary portfolio(Authentication authentication) {
        return portfolioService.summarize(authentication.getName());
    }
}
