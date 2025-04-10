package com.martingago.words.domain.repository;

import com.martingago.words.domain.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserModel, Long> {

    Optional<UserModel> findUserByUsername(String username);
}
