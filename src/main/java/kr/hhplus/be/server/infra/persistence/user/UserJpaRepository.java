package kr.hhplus.be.server.infra.persistence.user;

import kr.hhplus.be.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {
}
