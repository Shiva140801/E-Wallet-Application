package org.wallet.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class WalletBalanceResponseDTO {
    private String status;
    private Integer code;
    private String message;
    private WalletData data;

    @Data
    @Builder
    public static class WalletData {
        private UUID walletId;
        private UUID userId;
        private String walletType;
        private BigDecimal balance;
        private String currency;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        private LocalDateTime lastUpdated;
    }
}