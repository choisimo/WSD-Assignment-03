package com.nodove.WSD_Assignment_03.configuration.token.principalDetails;

import com.nodove.WSD_Assignment_03.domain.users;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@Slf4j
@RequiredArgsConstructor
public class principalDetails implements UserDetails {

    private final users user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !user.isBlocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        log.info("check if user is deleted : {}", user.isDeleted());
        return !user.isDeleted();
    }

    public String getUserId() {
        return user.getUserId();
    }

    public String getNickname() {
        return user.getNickname();
    }

}
