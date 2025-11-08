// src/main/java/com/example/walletapi/repository/WalletDetailsRepository.java
package org.wallet.repository;

import org.wallet.model.WalletDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletDetailsRepository extends JpaRepository<WalletDetails, UUID> {
    // Custom method to find the active wallet for a specific user ID
    Optional<WalletDetails> findByUserIdAndIsActiveTrue(UUID userId);
}