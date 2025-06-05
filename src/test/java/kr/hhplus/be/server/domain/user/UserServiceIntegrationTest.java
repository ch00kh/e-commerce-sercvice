package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.surpport.cleaner.DatabaseClearExtension;
import kr.hhplus.be.server.domain.user.dto.UserCommand;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.global.exception.ErrorCode;
import kr.hhplus.be.server.global.exception.GlobalException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ExtendWith(DatabaseClearExtension.class)
@ActiveProfiles("test")
@DisplayName("[통합테스트] UserService")
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("사용자를 생성한 후 조회한다.")
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
    @DisplayName("이미 존재하는 사용자는 생성할 수 없다.")
    void create_badRequest() {

        // Arrange
        String userName = "추경현";
        userService.create(new UserCommand.Create(userName));

        // Act
        GlobalException exception = assertThrows(GlobalException.class, () -> userService.create(new UserCommand.Create(userName)));

        //Assert
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.BAD_REQUEST);
    }

    @Test
    @DisplayName("사용자ID가 없어 사용자를 조회할 수 없다.")
    void findNonExistingUser() {

        // Arrange
        UserCommand.Find command = new UserCommand.Find(9999L);

        // Act
        GlobalException exception = assertThrows(GlobalException.class, () -> userService.findByUserId(command));

        // Assert
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
    }
}