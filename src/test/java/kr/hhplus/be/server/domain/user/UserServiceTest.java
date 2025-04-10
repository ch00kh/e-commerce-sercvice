package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.domain.user.dto.UserCommand;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import kr.hhplus.be.server.global.exception.ErrorCode;
import kr.hhplus.be.server.global.exception.GlobalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("[단위테스트] UserService")
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    Long USER_ID;
    User USER;

    @BeforeEach
    void setUp() {
        USER_ID = 1L;
        USER = new User(USER_ID, "추경현", "chu@test.test");
    }

    @Test
    @DisplayName("[성공] 사용자 조회")
    void findByUserId() {

        // Arrange
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(USER));

        // Act
        User actualUser = userService.findByUserId(new UserCommand.Find(USER_ID));

        // Assert
        assertThat(actualUser.getId()).isEqualTo(1L);
        assertThat(actualUser.getName()).isEqualTo("추경현");
        assertThat(actualUser.getEmail()).isEqualTo("chu@test.test");
    }

    @Test
    @DisplayName("[실패] 사용자 조회 - 사용자 없음(NOT_FOUND)")
    void findByUserId_NotFound() {

        // Arrange
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        // Act
        GlobalException exception = assertThrows(GlobalException.class, () -> userService.findByUserId(new UserCommand.Find(USER_ID)));

        // Assert
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
    }
}