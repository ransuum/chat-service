package com.project.chat_service.utils.authentication_manager;

import com.project.chat_service.models.entity.Users;
import com.project.chat_service.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserConfiguration {
    private final UserRepository userRepository;

    public UserConfiguration(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Users currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(authentication.getName()).get();
    }
}
