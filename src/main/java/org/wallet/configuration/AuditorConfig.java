package org.wallet.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.wallet.utils.UtilService;

import java.util.Optional;
import java.util.UUID;

@Component("auditorAware")
public class AuditorConfig implements AuditorAware<UUID> {

    private final UtilService utilService;

    public AuditorConfig(UtilService utilService) {
        this.utilService = utilService;
    }


    @Override
    public Optional<UUID> getCurrentAuditor() {

        return Optional.of(UUID.randomUUID());
    }
}
