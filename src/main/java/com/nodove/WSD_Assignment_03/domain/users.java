package com.nodove.WSD_Assignment_03.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "users")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String userId; // 사용자 ID (고유값)

    @Column(nullable = false, length = 255)
    private String password; // 비밀번호 (암호화된 형태로 저장)

    @Column(nullable = false, unique = true, length = 100)
    private String email; // 이메일 (고유값)

    @Column(nullable = false, length = 30, unique = false)
    private String username; // 사용자 이름 (중복 가능)

    @Column(nullable = false, length = 30, unique = true)
    private String nickname; // 사용자 이름 (중복 불가능)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Role role; // 사용자 역할 (ADMIN, USER, COMPANY)

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 계정 생성 시간

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now(); // 계정 수정 시간

    private LocalDateTime deletedAt; // 삭제 시간 (NULL이면 활성 상태)

    @Column(nullable = false)
    private boolean isDeleted = false; // 삭제 여부 (TRUE이면 삭제 상태)

    @Column(nullable = false)
    private boolean isBlocked = false; // 차단 여부 (TRUE이면 차단 상태)

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(this.role.getKey()));
    }

}
