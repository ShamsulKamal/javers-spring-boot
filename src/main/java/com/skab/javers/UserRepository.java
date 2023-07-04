package com.skab.javers;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;

@JaversSpringDataAuditable
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
