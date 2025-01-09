package com.project.chat_service.repository;

import com.project.chat_service.models.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepo extends JpaRepository<Message, String> {
}
