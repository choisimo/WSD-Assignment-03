package com.nodove.WSD_Assignment_03.repository;

import com.nodove.WSD_Assignment_03.domain.users;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface usersRepository extends JpaRepository<users, Long> {

    Optional<users> findByUserId(String userId);

    Optional<Object> findByEmail(@Email @NotBlank String email);

    Optional<Object> findByNickname(@NotBlank String nickname);
}
