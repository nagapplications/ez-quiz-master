package com.naglabs.ezquizmaster.repository;

import com.naglabs.ezquizmaster.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSessionRepository extends JpaRepository<UserSession, String> {
}
