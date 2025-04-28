package kr.hhplus.be.server.domain.user.repository;

import kr.hhplus.be.server.domain.user.entity.User;

import java.util.Optional;

public interface UserRepository {

    User findById(Long userId);

    User save(User user);

    Optional<User> findByName(String name);
}
