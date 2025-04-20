package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.domain.user.dto.UserCommand;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.global.exception.ErrorCode;
import kr.hhplus.be.server.global.exception.GlobalException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("[통합테스트] UserService")
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("[성공] 사용자 생성 및 조회")
    void createAndFindUser() {

        // Arrange
        String userName = "추경현";

        // Act
        User createdUser = userService.create(new UserCommand.Create(userName));
        User foundUser = userService.findByUserId(new UserCommand.Find(createdUser.getId()));

        // Assert
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(createdUser.getId());
        assertThat(foundUser.getName()).isEqualTo(userName);
    }

    @Test
    @DisplayName("[실패] 사용자 조회 -> 사용자 없음 예외 (NOT_FOUND)")
    void findNonExistingUser() {

        // Arrange
        UserCommand.Find command = new UserCommand.Find(9999L);

        // Act
        GlobalException exception = assertThrows(GlobalException.class, () -> userService.findByUserId(command));

        // Assert
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
    }
}