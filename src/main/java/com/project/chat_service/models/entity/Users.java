package com.project.chat_service.models.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.relational.core.mapping.Table;
import jakarta.persistence.Entity;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "users")
public class Users implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private String id;

    @Column(nullable = false, name = "firstname")
    private String firstname;

    @Column(nullable = false, name = "lastname")
    private String lastname;

    @Column(unique = true, nullable = false)
    private String phone;

    @Column(nullable = false, name = "username", unique = true)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @JsonIgnore
    private String roles;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<RefreshToken> refreshTokens;

    @CreatedDate
    private LocalDateTime createdAt;

    private String address;

    @Formula("(SELECT c.id FROM chat_info c WHERE c.first_user_id = id OR c.second_user_id = id)")
    @OneToMany
    @JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
    private List<Chat> chats;
}
