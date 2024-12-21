package com.nodove.WSD_Assignment_03.repository;

import com.nodove.WSD_Assignment_03.domain.userLoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface userLoginHistoryRepository extends JpaRepository<userLoginHistory, Long> {
}
