package com.applix.shop.users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "userswing")
@Getter
@Setter
public class User {

    @Id
    @Column(name = "id_user")
    private Long id;

    @Column(name = "login")
    private String login;

    @Column(name = "name")
    private String name;

    // Легаси-хэш: hex(SHA-1(rawPassword + "{" + salt + "}")), см. LegacyShaPasswordVerifier.
    // Поле `pass` в этой же таблице не используется для проверки логина (уточнено с пользователем).
    @Column(name = "password")
    private String password;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "admin")
    private Integer admin;
}
