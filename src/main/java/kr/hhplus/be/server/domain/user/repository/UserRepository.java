package kr.hhplus.be.server.domain.user.repository;

import kr.hhplus.be.server.domain.user.entity.User;

public interface UserRepository {

    User findById(Long userId);

    User save(User user);
}
