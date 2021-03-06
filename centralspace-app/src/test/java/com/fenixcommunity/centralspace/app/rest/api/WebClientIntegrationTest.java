package com.fenixcommunity.centralspace.app.rest.api;

import static com.fenixcommunity.centralspace.app.configuration.security.SecurityUserGroup.ADMIN_USER;
import static com.fenixcommunity.centralspace.app.configuration.security.SecurityUserGroup.BASIC_USER;
import static com.fenixcommunity.centralspace.app.rest.api.WebClientLuxIntegrationTest.ADMIN_TEST;
import static com.fenixcommunity.centralspace.app.rest.api.WebClientLuxIntegrationTest.BASIC_USER_TEST;
import static com.fenixcommunity.centralspace.utilities.common.Var.PASSWORD_HIGH;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.context.jdbc.SqlConfig.TransactionMode.DEFAULT;
import static org.springframework.util.Assert.isInstanceOf;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

import java.time.ZonedDateTime;

import com.fenixcommunity.centralspace.app.configuration.CentralspaceApplicationConfig;
import com.fenixcommunity.centralspace.app.configuration.restcaller.RestCallerStrategy;
import com.fenixcommunity.centralspace.app.service.account.AccountService;
import com.fenixcommunity.centralspace.domain.model.permanent.account.Account;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = {CentralspaceApplicationConfig.class})
//todo replace from CentralspaceApplicationConfig to CentralspaceApplicationConfigTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SqlGroup({
        @Sql(scripts = {"classpath:/script/schema_integration_test.sql"},
                executionPhase = BEFORE_TEST_METHOD,
                config = @SqlConfig(encoding = "utf-8", transactionMode = DEFAULT)
        )
})
class WebClientIntegrationTest {
    private static final String BASE_ACCOUNT_URL = "/api/account/";
    private static final String BASE_LOGGER_URL = "/api/logger/";
    private static final String APP_PATH = "/app";

    private WebTestClient adminClient;
    private WebTestClient basicClient;

    @Autowired
    private RestCallerStrategy restCallerStrategy;

    @LocalServerPort
    private String port;

    @MockBean
    private AccountService accountService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @BeforeEach
    public void init() {
        this.basicClient = setOptions(BASIC_USER_TEST);
        this.adminClient = setOptions(ADMIN_TEST);
    }

    private WebTestClient setOptions(String user) {
        WebTestClient webTestClient = WebTestClient
                .bindToServer().baseUrl("http://localhost:" + port + APP_PATH)
//              .bindToController(new TestController()) -> custom controller
                .filter(basicAuthentication(user, PASSWORD_HIGH))
                .build();
        webTestClient.options()
                .accept(MediaType.ALL)
                .headers(httpHeaders -> {
                    httpHeaders.setDate(ZonedDateTime.now());
                })
                .cookies(cookie -> cookie.add("cookieTest", "cookieValue"));
//      webTestClient
//              .mutateWith(authentication(token))
//              .mutateWith(csrf())
        return webTestClient;
    }

    @Test
    void testBeans() {
        assertNotNull(adminClient);
        assertNotNull(basicClient);
        assertNotNull(restCallerStrategy.getWebClient());
    }

    @Test
    void testLoggerAsBasic() {
        basicClient.get()
                .uri(BASE_LOGGER_URL + "test")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
//                .expectBody()
//                .jsonPath("$.message").isEqualTo("error"); // if array [0].message
    }

    /*      @Test(expected = WebClientResponseException.class)
            or exceptionRule -> junit4*/
    @Test
    void testWebClientExceptionByRealWebClient() {
        WebClientException exception = assertThrows(WebClientException.class, () -> {
            WebClient.create()
                    .get()
                    .uri("http://localhost:" + port + APP_PATH + BASE_ACCOUNT_URL + "all")
                    .headers(httpHeaders -> httpHeaders.setBearerAuth(PASSWORD_HIGH))
                    .retrieve()
                    .bodyToMono(Account.class)
                    .block();
        });
        isInstanceOf(WebClientResponseException.class, exception);
        WebClientResponseException webClientException = (WebClientResponseException) exception;
        isTrue(webClientException.getStatusCode() == HttpStatus.UNAUTHORIZED);


    }
}
