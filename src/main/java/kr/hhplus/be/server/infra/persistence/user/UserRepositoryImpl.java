package kr.hhplus.be.server.infra.persistence.user;

import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import kr.hhplus.be.server.global.exception.ErrorCode;
import kr.hhplus.be.server.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;

    @Override
    public User findById(Long userId) {
        return jpaRepository.findById(userId).orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
    }

    @Override
    public User save(User user) {
        return jpaRepository.save(user);
    }

}
