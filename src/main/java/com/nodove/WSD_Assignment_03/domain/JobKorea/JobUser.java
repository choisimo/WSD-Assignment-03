package com.nodove.WSD_Assignment_03.domain.JobKorea;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class JobUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;   // 사용자 이름
    private String email;      // 이메일
    private String password;   // 암호화된 비밀번호

    @OneToMany(mappedBy = "user")
    private List<Application> applications; // 지원 내역

    @OneToMany(mappedBy = "user")
    private List<UserBookmark> bookmarks; // 사용자의 북마크 목록


}
