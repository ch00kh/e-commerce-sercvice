package kr.hhplus.be.server.application.user;

import kr.hhplus.be.server.application.user.dto.UserCriteria;
import kr.hhplus.be.server.application.user.dto.UserResult;
import kr.hhplus.be.server.domain.balance.entity.Balance;
import kr.hhplus.be.server.domain.balance.repository.BalanceRepository;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class UserFacadeTest {

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BalanceRepository balanceRepository;

    @Test
    @DisplayName("[성공] 사용자 생성 - 잔고도 같이 생성")
    void createUser_ShouldCreateUserAndBalance() {

        // Given
        UserCriteria.Create criteria = new UserCriteria.Create("추경현");

        // When
        UserResult.Create result = userFacade.createUser(criteria);

        // Then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("추경현");
        assertThat(result.balance()).isEqualTo(0);

        User user = userRepository.findById(result.id()).get();
        assertThat(user.getName()).isEqualTo("추경현");

        Balance balance = balanceRepository.findByUserId(result.id()).get();
        assertThat(balance.getBalance()).isEqualTo(0);
    }

}