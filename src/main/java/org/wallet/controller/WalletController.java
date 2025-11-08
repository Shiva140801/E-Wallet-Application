package org.wallet.controller;

import org.wallet.dto.WalletBalanceResponseDTO;
import org.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @GetMapping("/check-balance/{userId}")
    public ResponseEntity<WalletBalanceResponseDTO> checkWalletBalance(
            @PathVariable String userId,
            @RequestHeader(value = "Content-Type", defaultValue = "application/json") String contentTypeHeader) {

        WalletBalanceResponseDTO response = walletService.getWalletBalance(UUID.fromString(userId));
        return ResponseEntity.ok(response);
    }
}