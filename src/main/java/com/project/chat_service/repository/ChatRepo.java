package com.project.chat_service.repository;

import com.project.chat_service.models.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRepo extends JpaRepository<Chat, String> {
    Optional<Chat> findByFirstUser_Username(String username);
}
