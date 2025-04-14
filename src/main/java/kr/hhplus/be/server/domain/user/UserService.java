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

    @Transactional(readOnly = true)
    public User findByUserId(UserCommand.Find command) {
        return userRepository.findById(command.id())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
    }

    public User create(UserCommand.Create command) {
        return  userRepository.save(new User(command.name()));
    }
}
