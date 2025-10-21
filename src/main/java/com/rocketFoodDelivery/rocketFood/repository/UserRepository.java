package com.rocketFoodDelivery.rocketFood.repository;

import com.rocketFoodDelivery.rocketFood.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    // ✅ Retrieve a user by their ID using native SQL
    @Query(nativeQuery = true, value = "SELECT * FROM users WHERE id = :id")
    Optional<UserEntity> findById(int id);

    // ✅ Retrieve all users in descending order by ID
    List<UserEntity> findAllByOrderByIdDesc();

    // ✅ Retrieve a user by email
    Optional<UserEntity> findByEmail(String email);
}
