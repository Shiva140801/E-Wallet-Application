package org.wallet.users.service;

import org.springframework.stereotype.Service;
import org.wallet.users.repo.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
