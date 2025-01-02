package com.project.chat_service.security.config;

import com.project.chat_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInfoManagerConfig implements UserDetailsService {

    private final UserRepository usersRepo;

    @Override
    public UserDetails loadUserByUsername(String emailId) throws UsernameNotFoundException {
        return usersRepo.findByEmail(emailId)
                .map(UserInfoConfig::new)
                .orElseThrow(() -> new UsernameNotFoundException("UserEmail: " + emailId + " does not exist"));
    }
}
