package org.wallet.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.wallet.users.model.User;
import org.wallet.users.repo.UserRepository;

import java.util.UUID;

@Service
public class UtilService {

    private final UserRepository userRepository;

    public UtilService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public UUID getCurrentUserId() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElse(null);

            return user != null ? user.getId() : null;
        }

        return null;
    }
}
