package com.nodove.WSD_Assignment_03.domain;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 권한 코드와 권한 이름을 저장하는 Enum 클래스

@Getter
@RequiredArgsConstructor
public enum Role {
    USER("ROLE_USER", "일반사용자"),
    ADMIN("ROLE_ADMIN", "관리자");

    private final String key;
    private final String title;
}
