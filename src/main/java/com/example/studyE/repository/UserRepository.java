package com.example.studyE.repository;

import com.example.studyE.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    @Query("SELECT u FROM User u WHERE u.uid = :uid")
    Optional<User> findByUid(@Param("uid") String uid);

}

