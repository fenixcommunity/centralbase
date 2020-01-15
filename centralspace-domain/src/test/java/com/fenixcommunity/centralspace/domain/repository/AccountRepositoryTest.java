package com.fenixcommunity.centralspace.domain.repository;

import com.fenixcommunity.centralspace.domain.configuration.DomainConfigForTest;
import com.fenixcommunity.centralspace.domain.model.mounted.account.Account;
import com.fenixcommunity.centralspace.domain.repository.mounted.AccountRepository;
import lombok.experimental.FieldDefaults;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

import static com.fenixcommunity.centralspace.utilities.common.Var.*;
import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.context.jdbc.SqlConfig.TransactionMode.DEFAULT;

@RunWith(SpringRunner.class)
@DataJpaTest
/*If you want to use Spring Custom Method ..findByLogin please extend to:
@AutoConfigureTestEntityManager
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)*/
@TestPropertySource(locations = {"classpath:domain-test.properties"})
@ContextConfiguration(classes = DomainConfigForTest.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SqlGroup({
        @Sql(scripts = {"classpath:/script/schema-test.sql"},
                executionPhase = BEFORE_TEST_METHOD,
                config = @SqlConfig(encoding = "utf-8", transactionMode = DEFAULT)
        )
})
@FieldDefaults(level = PRIVATE)
public class AccountRepositoryTest {

    private static final long ACCOUNT_ID_FROM_QUERY = 99L;

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private AccountRepository accountRepository;

    // used junit4, no jupiter
    @Before
    public void init() {
        // magic, we don't need class var in Before method
        Account account = Account.builder()
                .login(LOGIN)
                .mail(MAIL).build();
        testEntityManager.persistAndGetId(account);
        testEntityManager.flush();
    }

    @Test
    public void repoInitTest() {
        assertNotNull(testEntityManager);
        assertNotNull(accountRepository);
        assertNotNull(dataSource);
    }

    @Test
    public void repoTest() {
        assertNotNull(accountRepository.findById(ID));
        assertNotNull(accountRepository.findByLogin(LOGIN));
    }

    @Test
    public void repoExtractingTest() {
        List<Account> accounts = accountRepository.findAll();
        assertNotNull(accounts);
        assertThat(accounts).extracting(Account::getLogin).containsAnyOf(LOGIN);
    }

    @Test
    public void repoFromExecutedQueryTest() {
        Optional<Account> foundAccount = accountRepository.findById(ACCOUNT_ID_FROM_QUERY);
//      assertNotNull(accountRepository.findByLogin("loginQuery"));  // no works, only when we persist in code
        assertNotNull(foundAccount.orElse(null));
    }
}