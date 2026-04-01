package com.bike.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "app_user")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    /**
     * user / admin
     */
    private String role;

    private String nickname;

    private String phone;

    @Column(name = "create_time")
    private LocalDateTime createTime;
}