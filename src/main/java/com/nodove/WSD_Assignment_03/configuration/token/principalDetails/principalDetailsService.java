package com.nodove.WSD_Assignment_03.configuration.token.principalDetails;

import com.nodove.WSD_Assignment_03.repository.usersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class principalDetailsService implements UserDetailsService {
    private final usersRepository usersRepository;


    // username 이지만 userId로 변경 (DB에 저장된 userId로 로그인 // username은 중복 가능 값)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usersRepository.findByUserId(username)
                .map(principalDetails::new) // PrincipalDetails 생성자 확인
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
