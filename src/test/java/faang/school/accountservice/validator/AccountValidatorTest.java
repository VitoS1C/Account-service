package faang.school.accountservice.validator;

import faang.school.accountservice.model.entity.Account;
import faang.school.accountservice.model.entity.Balance;
import faang.school.accountservice.model.enums.AccountStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AccountValidatorTest {

    private AccountValidator accountValidator;
    private Account account;
    private Balance balance;

    @BeforeEach
    void setUp() {
        accountValidator = new AccountValidator();
        account = mock(Account.class);
        balance = mock(Balance.class);
    }

    @Test
    void validateAccountToBlock_shouldThrowExceptionIfOwnerIsNotSame() {
        //given
        when(account.getOwnerId()).thenReturn(1L);
        long anotherOwnerId = 2L;
        // when & then
        assertThatThrownBy(() -> accountValidator.validateAccountToBlock(account, anotherOwnerId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Only owner can block this account");
    }

    @Test
    void validateAccountToBlock_shouldThrowExceptionIfAccountAlreadyBlocked() {
        // given
        when(account.getOwnerId()).thenReturn(1L);
        when(account.getAccountStatus()).thenReturn(AccountStatus.BLOCKED);
        //when & then
        assertThatThrownBy(() -> accountValidator.validateAccountToBlock(account, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("This account is already blocked");
    }

    @Test
    void validateAccountToBlock_shouldNotThrowExceptionIfAccountIsValid() {
        // given
        when(account.getOwnerId()).thenReturn(1L);
        when(account.getAccountStatus()).thenReturn(AccountStatus.ACTIVE);
        //when & then
        assertThatCode(() -> accountValidator.validateAccountToBlock(account, 1L))
                .doesNotThrowAnyException();
    }

    @Test
    void validateAccountToClose_shouldThrowExceptionIfOwnerIsNotSame() {
        //given
        when(account.getOwnerId()).thenReturn(1L);
        long anotherOwnerId = 2L;
        // when & then
        assertThatThrownBy(() -> accountValidator.validateAccountToClose(account, anotherOwnerId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Only owner can close this account");
    }

    @Test
    void validateAccountToClose_shouldNotThrowExceptionIfAccountIsValid() {
        // given
        when(account.getOwnerId()).thenReturn(1L);
        when(account.getBalance()).thenReturn(balance);
        when(balance.getCurrentActualBalance()).thenReturn(BigDecimal.ZERO);
        // when & then
        assertThatCode(() -> accountValidator.validateAccountToClose(account, 1L))
                .doesNotThrowAnyException();
    }

    @Test
    void validateAccountToClose_shouldThrowExceptionIfBalanceIsNotZero() {
        // given
        when(account.getOwnerId()).thenReturn(1L);
        when(account.getBalance()).thenReturn(balance);
        when(balance.getCurrentActualBalance()).thenReturn(BigDecimal.valueOf(100));
        // when & then
        assertThatThrownBy(() -> accountValidator.validateAccountToClose(account, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Current actual balance is not zero");
    }
}
