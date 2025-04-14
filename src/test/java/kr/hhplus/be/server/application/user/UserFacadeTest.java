package kr.hhplus.be.server.application.user;

import kr.hhplus.be.server.application.user.dto.UserCriteria;
import kr.hhplus.be.server.application.user.dto.UserResult;
import kr.hhplus.be.server.domain.balance.BalanceService;
import kr.hhplus.be.server.domain.balance.dto.BalanceCommand;
import kr.hhplus.be.server.domain.balance.entity.Balance;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.domain.user.dto.UserCommand;
import kr.hhplus.be.server.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("[통합테스트] UserFacade")
class UserFacadeTest {

    @InjectMocks
    private UserFacade userFacade;

    @Mock
    private UserService userService;

    @Mock
    private BalanceService balanceService;

    @Test
    @DisplayName("[성공] 사용자 생성 - 잔고도 같이 생성")
    void createUser() {

        // Arrange
        UserCommand.Create userCommand = new UserCommand.Create(1L, "추경현");
        when(userService.create(userCommand)).thenReturn(new User(1L, "추경현"));

        BalanceCommand.Create balanceCommand = new BalanceCommand.Create(1L);
        when(balanceService.create(balanceCommand)).thenReturn(new Balance(1L, 0L));

        // Act
        UserResult.Create actual = userFacade.createUser(new UserCriteria.Create(1L, "추경현"));

        // Assert
        assertThat(actual.id()).isEqualTo(1L);
        assertThat(actual.name()).isEqualTo("추경현");
        assertThat(actual.balance()).isEqualTo(0L);
    }

}