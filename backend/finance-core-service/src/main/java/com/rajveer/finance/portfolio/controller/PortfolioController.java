package com.rajveer.finance.portfolio.controller;

import com.rajveer.finance.common.enums.AssetType;
import com.rajveer.finance.common.response.ApiResponse;
import com.rajveer.finance.portfolio.dto.AssetAllocationResponse;
import com.rajveer.finance.portfolio.dto.PortfolioAssetRequest;
import com.rajveer.finance.portfolio.dto.PortfolioAssetResponse;
import com.rajveer.finance.portfolio.dto.PortfolioSummaryResponse;
import com.rajveer.finance.portfolio.service.PortfolioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @PostMapping("/assets")
    public ResponseEntity<ApiResponse<PortfolioAssetResponse>>
    createAsset(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody
            PortfolioAssetRequest request
    ) {
        PortfolioAssetResponse response =
                portfolioService.createAsset(
                        extractUserId(jwt),
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Portfolio asset created successfully",
                                response
                        )
                );
    }

    @GetMapping("/assets")
    public ResponseEntity<ApiResponse<List<PortfolioAssetResponse>>>
    getAssets(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false)
            AssetType assetType
    ) {
        List<PortfolioAssetResponse> response =
                portfolioService.getAssets(
                        extractUserId(jwt),
                        assetType
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Portfolio assets retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/assets/{assetId}")
    public ResponseEntity<ApiResponse<PortfolioAssetResponse>>
    getAssetById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long assetId
    ) {
        PortfolioAssetResponse response =
                portfolioService.getAssetById(
                        extractUserId(jwt),
                        assetId
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Portfolio asset retrieved successfully",
                        response
                )
        );
    }

    @PutMapping("/assets/{assetId}")
    public ResponseEntity<ApiResponse<PortfolioAssetResponse>>
    updateAsset(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long assetId,
            @Valid @RequestBody
            PortfolioAssetRequest request
    ) {
        PortfolioAssetResponse response =
                portfolioService.updateAsset(
                        extractUserId(jwt),
                        assetId,
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Portfolio asset updated successfully",
                        response
                )
        );
    }

    @DeleteMapping("/assets/{assetId}")
    public ResponseEntity<ApiResponse<Void>>
    deleteAsset(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long assetId
    ) {
        portfolioService.deleteAsset(
                extractUserId(jwt),
                assetId
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Portfolio asset deleted successfully"
                )
        );
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<PortfolioSummaryResponse>>
    getPortfolioSummary(
            @AuthenticationPrincipal Jwt jwt
    ) {
        PortfolioSummaryResponse response =
                portfolioService.getPortfolioSummary(
                        extractUserId(jwt)
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Portfolio summary retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/allocation")
    public ResponseEntity<ApiResponse<List<AssetAllocationResponse>>>
    getAssetAllocation(
            @AuthenticationPrincipal Jwt jwt
    ) {
        List<AssetAllocationResponse> response =
                portfolioService.getAssetAllocation(
                        extractUserId(jwt)
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Asset allocation retrieved successfully",
                        response
                )
        );
    }

    private Long extractUserId(Jwt jwt) {
        Number userId = jwt.getClaim("userId");

        if (userId == null) {
            throw new IllegalStateException(
                    "Authenticated token does not contain a user ID"
            );
        }

        return userId.longValue();
    }
}