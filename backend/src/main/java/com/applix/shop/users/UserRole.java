package com.applix.shop.users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_roles")
@Getter
@Setter
public class UserRole {

    @Id
    @Column(name = "idRole")
    private Integer id;

    @Column(name = "authority")
    private String authority;
}
