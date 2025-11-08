package org.wallet.service;

import org.wallet.dto.WalletBalanceResponseDTO;
import org.wallet.exception.ResourceNotFoundException;
import org.wallet.exception.exception;
import org.wallet.model.WalletDetails;
import org.wallet.repository.WalletDetailsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WalletServiceTest {

    @Mock
    private WalletDetailsRepository walletDetailsRepository;

    @InjectMocks
    private WalletService walletService;

    private UUID userId;
    private WalletDetails wallet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();

        wallet = new WalletDetails();
        wallet.setId(UUID.randomUUID());
        wallet.setUserId(userId);
        wallet.setWalletType("MAIN");
        wallet.setAvailableBalance(BigDecimal.valueOf(500.00));
        wallet.setIsActive(true);
        wallet.setCurrency("USD");
        wallet.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testGetWalletBalance_Success() {
        when(walletDetailsRepository.findByUserIdAndIsActiveTrue(userId))
                .thenReturn(Optional.of(wallet));

        WalletBalanceResponseDTO response = walletService.getWalletBalance(userId);

        assertNotNull(response);
        assertEquals("success", response.getStatus());
        assertEquals(200, response.getCode());
        assertEquals("Wallet balance fetched successfully", response.getMessage());
        assertNotNull(response.getData());
        assertEquals(userId, response.getData().getUserId());
        assertEquals(wallet.getAvailableBalance(), response.getData().getBalance());

        verify(walletDetailsRepository, times(1))
                .findByUserIdAndIsActiveTrue(userId);
    }

    @Test
    void testGetWalletBalance_WalletNotFound() {
        when(walletDetailsRepository.findByUserIdAndIsActiveTrue(userId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> walletService.getWalletBalance(userId));

        verify(walletDetailsRepository, times(1))
                .findByUserIdAndIsActiveTrue(userId);
    }
}
