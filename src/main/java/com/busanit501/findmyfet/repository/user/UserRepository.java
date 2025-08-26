package com.busanit501.findmyfet.repository.user;

import com.busanit501.findmyfet.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
