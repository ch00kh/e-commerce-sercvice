package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.domain.user.dto.UserCommand;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import kr.hhplus.be.server.global.exception.ErrorCode;
import kr.hhplus.be.server.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * 사용자 조회
     */
    public User findByUserId(UserCommand.Find command) {
        return userRepository.findById(command.id());
    }

    /**
     * 사용자 생성
     */
    @Transactional
    public User create(UserCommand.Create command) {

        userRepository.findByName(command.name()).ifPresent(user -> {
            throw new GlobalException(ErrorCode.BAD_REQUEST);
        });

        return userRepository.save(new User(command.name()));
    }

}
