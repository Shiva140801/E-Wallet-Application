package org.wallet.service;

import org.wallet.dto.WalletBalanceResponseDTO;
import org.wallet.exception.exception;
import org.wallet.model.WalletDetails;
import org.wallet.repository.WalletDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class WalletService {

    @Autowired
    private WalletDetailsRepository walletDetailsRepository;

    public WalletBalanceResponseDTO getWalletBalance(UUID userId) {
        WalletDetails wallet = walletDetailsRepository.findByUserIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new exception("Active wallet not found for user: " + userId));

        WalletBalanceResponseDTO.WalletData data = WalletBalanceResponseDTO.WalletData.builder()
                .walletId(wallet.getId())
                .userId(userId)
                .walletType(wallet.getWalletType())
                .balance(wallet.getAvailableBalance())
                .currency(wallet.getCurrency())
                .lastUpdated(wallet.getUpdatedAt())
                .build();

        return WalletBalanceResponseDTO.builder()
                .status("success")
                .code(200)
                .message("Wallet balance fetched successfully")
                .data(data)
                .build();
    }
}