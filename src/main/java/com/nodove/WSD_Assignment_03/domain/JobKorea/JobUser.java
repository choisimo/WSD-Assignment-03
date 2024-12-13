package com.nodove.WSD_Assignment_03.domain.JobKorea;

import com.nodove.WSD_Assignment_03.domain.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "`JobUser`")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;   // 사용자 이름
    @Column(nullable = false, unique = true)
    private String email;      // 이메일
    @Column(nullable = false)
    private String password;   // 암호화된 비밀번호
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Role role = Role.USER; // 사용자 권한
    @OneToMany(mappedBy = "user")
    private List<Application> applications; // 지원 내역

    @OneToMany(mappedBy = "user")
    private List<UserBookmark> bookmarks; // 사용자의 북마크 목록


}
