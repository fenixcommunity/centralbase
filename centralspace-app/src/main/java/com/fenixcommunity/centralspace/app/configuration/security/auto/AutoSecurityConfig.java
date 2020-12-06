package com.fenixcommunity.centralspace.app.configuration.security.auto;

import static com.fenixcommunity.centralspace.app.configuration.security.auto.SecurityRole.ADMIN;
import static com.fenixcommunity.centralspace.app.configuration.security.auto.SecurityRole.BASIC;
import static com.fenixcommunity.centralspace.app.configuration.security.auto.SecurityRole.FLUX_EDITOR;
import static com.fenixcommunity.centralspace.app.configuration.security.auto.SecurityRole.FLUX_GETTER;
import static com.fenixcommunity.centralspace.app.configuration.security.auto.SecurityRole.SWAGGER;
import static com.fenixcommunity.centralspace.utilities.common.DevTool.listsTo1Array;
import static com.fenixcommunity.centralspace.utilities.common.DevTool.mergeStringArrays;
import static com.fenixcommunity.centralspace.utilities.common.Var.PASSWORD;
import static lombok.AccessLevel.PRIVATE;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

import com.fenixcommunity.centralspace.app.configuration.security.auto.handler.AppAuthenticationFailureHandler;
import com.fenixcommunity.centralspace.app.configuration.security.auto.handler.AppAuthenticationSuccessHandler;
import com.fenixcommunity.centralspace.app.configuration.security.auto.handler.AppLogoutSuccessHandler;
import com.fenixcommunity.centralspace.app.service.security.auto.LoginAttemptService;
import com.fenixcommunity.centralspace.utilities.common.DevTool;
import de.codecentric.boot.admin.server.config.AdminServerProperties;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity // (debug = true)
//@EnableWebFluxSecurity todo https://www.baeldung.com/spring-security-5-reactive
@ComponentScan({"com.fenixcommunity.centralspace.app.service.security"})
@FieldDefaults(level = PRIVATE, makeFinal = true)
public abstract class AutoSecurityConfig {

    private static final String API_PATH = "/api";
    private static final String REMEMBER_ME_COOKIE = "remembermecookie";
    private static final int SESSION_TIMEOUT_SECONDS = 60 * 10; //todo to properties
    private static final int TOKEN_VALIDITY_SECONDS = 60 * 45;

    private static final String[] ADMIN_API_AUTH_LIST = {
            API_PATH + "/account/**",
            API_PATH + "/aws/**",
            API_PATH + "/account-flux/**",
            API_PATH + "/doc/**",
            API_PATH + "/mail/**",
            API_PATH + "/password/**",
            API_PATH + "/register/**",
            API_PATH + "/metrics/**",
            API_PATH + "/async/**",
            API_PATH + "/customization/**",
            API_PATH + "/sms-sender/**",
            API_PATH + "/app-control/**",
            API_PATH + "/batch/**",
            API_PATH + "/features/**"
    };
    private static final String[] BASIC_API_AUTH_LIST = {
            API_PATH + "/resource-cache/**"
    };
    private static final String[] FLUX_API_AUTH_LIST = {
            API_PATH + "/account-flux/**"
    };
    private static final String[] NO_AUTH_API_LIST = {
            API_PATH + "/logger/basic-info",
            API_PATH + "/cross/**"
    };
    //FORM
    private static final String[] ADMIN_FORM_AUTH_LIST = {
            "/h2-console/**",
            "/actuator/**",
            "/prometheus/**"
    };
    private static final String[] NO_AUTH_FORM_LIST = {
            API_PATH + "/logger/test",
            "/public/**"
    };
    private static final String[] SWAGGER_AUTH_LIST = {
            "/swagger",
            "/v2/api-docs",
            "/configuration/ui",
            "/swagger-resources",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**"
    };

    private final DataSource dataSource;

    public AutoSecurityConfig(final @Qualifier("h2DataSource") DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Autowired
    public void configureGlobal(final AuthenticationManagerBuilder auth, final PasswordEncoder passwordEncoder) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery(getUserQuery())
                .authoritiesByUsernameQuery(getAuthoritiesQuery())
                .groupAuthoritiesByUsername(groupAuthoritiesByUsername())
                .passwordEncoder(passwordEncoder);
        // to testing authenticationSuccessListener
        auth.eraseCredentials(false);

//              .withUser(User.withUsername(DB_USER.name())
//                        .password(passwordEncoder().encode(PASSWORD))
//                        .roles(DB_USER.name())
//                        .authorities(listsTo1Array(DB_USER.getRoles()))
        auth.inMemoryAuthentication()
                .withUser(SWAGGER.name())
                .password(passwordEncoder.encode(PASSWORD))
                .roles(listsTo1Array(SWAGGER.getRoles()))
/*              .and()   -> we do this step by jdbcAuthentication
                .withUser(DB_USER.name())
                .password(passwordEncoder.encode(PASSWORD))
                .roles(listsTo1Array(DB_USER.getRoles()))*/
                .and()
                .withUser(FLUX_GETTER.name())
                .password(passwordEncoder.encode(PASSWORD))
                .roles(listsTo1Array(FLUX_GETTER.getRoles()))
                .and()
                .withUser(FLUX_EDITOR.name())
                .password(passwordEncoder.encode(PASSWORD))
                .roles(listsTo1Array(FLUX_EDITOR.getRoles()))
                .and()
                .withUser(BASIC.name())
                .password(passwordEncoder.encode(PASSWORD))
                .roles(listsTo1Array(BASIC.getRoles()))
                .and()
                .withUser(ADMIN.name())
                .password(passwordEncoder.encode(PASSWORD))
                .roles(listsTo1Array(ADMIN.getRoles()));
    }

/*    @Bean
    public JdbcUserDetailsManager jdbcUserDetailsManager() {
        return new JdbcUserDetailsManager(dataSource);
    }  ->  in service we can: jdbcUserDetailsManager.createUser(
      new User(myUser.getUserName(), encodededPassword, singletonList(new SimpleGrantedAuthority(myUser.getRoles()))))*/

    private String getUserQuery() {
        return "select username as username, password as password, enabled as enabled "
                + "from users "
                + "where username = ? ";
    }

    private String getAuthoritiesQuery() {
        return "select username, authority from authorities where username=?";
/*   or extend (other option to: groupAuthoritiesByUsername)
     "SELECT * FROM AUTHORITIES WHERE AUTHORITY_ID IN( "+
                "SELECT DISTINCT AUTHORITY_ID FROM ROLES_AUTHORITIES  S1 "+
                "JOIN USERS_ROLES S2 ON S1.ROLE_ID = S2.ROLE_ID "+
                "JOIN USERS S3 ON S3.USER_ID = S2.USER_ID AND S3.USERNAME=?)";*/
    }

    private String groupAuthoritiesByUsername() {
        return "select g.id, g.group_name, ga.authority " +
                "from groups g, group_members gm, group_authorities ga " +
                "where gm.username = ? and g.id = ga.group_id and g.id = gm.group_id";
    }

    @Configuration
    @Order(1)
    public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
        private final AdminServerProperties adminServer;
        private final DataSource dataSource;

        @Autowired
        public FormLoginWebSecurityConfigurerAdapter(final AdminServerProperties adminServer,
                                                     final @Qualifier("h2DataSource") DataSource dataSource) {
            this.adminServer = adminServer;
            this.dataSource = dataSource;
        }

        @Override
        protected void configure(final HttpSecurity http) throws Exception {
            http
                    .exceptionHandling()
//                  .authenticationEntryPoint(appBasicAuthenticationEntryPoint())
                    .and()
                    //API
//                  .antMatcher(API_PATH + "/**").authorizeRequests()
                    .authorizeRequests()
                    .antMatchers(BASIC_API_AUTH_LIST).hasRole(BASIC.name())
                    .antMatchers(ADMIN_API_AUTH_LIST).hasRole(ADMIN.name())
                    .antMatchers(FLUX_API_AUTH_LIST).hasRole(FLUX_GETTER.name())
                    .antMatchers(this.adminServer.path("/assets/**")).permitAll()
                    .antMatchers(this.adminServer.path("/login")).permitAll()
                    .antMatchers(NO_AUTH_API_LIST).permitAll()
                    //FORM
                    .antMatchers(mergeStringArrays(SWAGGER_AUTH_LIST)).hasRole(SWAGGER.name())
                    .antMatchers(mergeStringArrays(ADMIN_FORM_AUTH_LIST)).hasRole(ADMIN.name())
                    .antMatchers(NO_AUTH_FORM_LIST).permitAll()
                    .anyRequest().hasRole(SWAGGER.name())
                    .and()
                    .cors()//.configurationSource(corsConfigurationSource())
                    .and()
//                    .csrf().disable()
                    .csrf()
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .ignoringRequestMatchers(
                            new AntPathRequestMatcher(this.adminServer.path("/instances"), HttpMethod.POST.toString()),
                            new AntPathRequestMatcher(this.adminServer.path("/instances/*"), HttpMethod.DELETE.toString()),
                            new AntPathRequestMatcher(this.adminServer.path("/actuator/**")))
                    .and()
                    .httpBasic()
                    .and()
                    .headers()
                    .frameOptions().sameOrigin()
                    .and()
                    .formLogin().permitAll()
                    .successHandler(appAuthenticationSuccessHandler(loginAttemptService(), passwordEncoder()))
                    .failureHandler(appAuthenticationFailureHandler())
                    .and()
                    .logout().logoutUrl("/logout") //todo frontend handle
                    .logoutSuccessHandler(logoutSuccessHandler())
                    .invalidateHttpSession(true)
                    .deleteCookies(REMEMBER_ME_COOKIE)
                    .and()
                    .rememberMe().key(DevTool.generateSecureToken())
                    .rememberMeCookieName(REMEMBER_ME_COOKIE)
                    .tokenValiditySeconds(TOKEN_VALIDITY_SECONDS)
                    .tokenRepository(tokenRepository())
                    .and()
                    .sessionManagement().maximumSessions(2)
                    .expiredUrl("/login?expired");
                 /*
                 .portMapper().http(9090).mapsTo(9443).http(80).mapsTo(443);
                 .deleteCookies("JSESSIONID");
                 .loginPage("/login")
                 .failureUrl("/login-error")
                 .loginProcessingUrl("/security_check")
                 .usernameParameter("username").passwordParameter("password")
                 .permitAll();
                  */
        }

        @Bean
        public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
            return new SecurityEvaluationContextExtension();
        }

        @Bean
        AuthenticationSuccessHandler appAuthenticationSuccessHandler(final LoginAttemptService loginAttemptService, final PasswordEncoder encoder) {
            return new AppAuthenticationSuccessHandler(loginAttemptService, encoder, SESSION_TIMEOUT_SECONDS, "/app/swagger-ui.html");
        }

        @Bean
        AuthenticationFailureHandler appAuthenticationFailureHandler() {
            return new AppAuthenticationFailureHandler();
        }

        @Bean
        public LogoutSuccessHandler logoutSuccessHandler() {
            return new AppLogoutSuccessHandler();
        }

        @Bean
        JdbcTokenRepositoryImpl tokenRepository() {
            final JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
            tokenRepository.setCreateTableOnStartup(false);
            tokenRepository.setDataSource(dataSource);
            return tokenRepository;
        }

        @Bean
        HttpSessionEventPublisher httpSessionEventPublisher() {
            return new HttpSessionEventPublisher();
        }

        @Bean
        PasswordEncoder passwordEncoder() {
            final PasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            final Map<String, PasswordEncoder> encoders = new HashMap<>();
            encoders.put("bcrypt", bCryptPasswordEncoder);
            encoders.put("scrypt", new SCryptPasswordEncoder());

            final DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder(
                    "bcrypt", encoders);
            passwordEncoder.setDefaultPasswordEncoderForMatches(bCryptPasswordEncoder);

            return passwordEncoder;
        }

        @Bean
        LoginAttemptService loginAttemptService() {
            return new LoginAttemptService();
        }
    }
}