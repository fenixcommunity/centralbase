package com.fenixcommunity.centralspace.app.configuration.security.autosecurity;

import com.fenixcommunity.centralspace.app.configuration.security.autosecurity.handler.AppAuthenticationFailureHandler;
import com.fenixcommunity.centralspace.app.configuration.security.autosecurity.handler.PreviousPageAuthenticationSuccessHandler;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;

import javax.sql.DataSource;

import static com.fenixcommunity.centralspace.app.configuration.security.autosecurity.SecurityRole.*;
import static com.fenixcommunity.centralspace.utilities.common.DevTool.listsTo1Array;
import static com.fenixcommunity.centralspace.utilities.common.DevTool.mergeStringArrays;
import static com.fenixcommunity.centralspace.utilities.common.Var.PASSWORD;
import static lombok.AccessLevel.PRIVATE;

@EnableWebSecurity // (debug = true)
@ComponentScan({"com.fenixcommunity.centralspace.app.service.security"})
//todo FieldDefaults private final??
@FieldDefaults(level = PRIVATE, makeFinal = true)
public abstract class AutoSecurityConfig {

    static String API_PATH = "/api";
    static String REMEMBER_ME_KEY = "9D119EE5A2B7DAF6B4DC1EF871D0AC3C";
    static String REMEMBER_ME_COOKIE = "remembermecookie";
    static int TOKEN_VALIDITY_SECONDS = 60 * 60;
    static String[] APP_AUTH_LIST = {
            API_PATH + "/account/**",
            API_PATH + "/doc/**",
            API_PATH + "/mail/**",
            API_PATH + "/password/**",
            API_PATH + "/register/**"
    };
    static String[] BASIC_AUTH_LIST = {
            API_PATH + "/resource/**"
    };
    static String[] NO_AUTH_LIST = {
            API_PATH + "/logger/**"
    };
    static String[] SWAGGER_AUTH_LIST = {
            "/v2/api-docs",
            "/configuration/ui",
            "/swagger-resources",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**"
    };

    DataSource dataSource;

    public AutoSecurityConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery(getUserQuery())
                .authoritiesByUsernameQuery(getAuthoritiesQuery())
                .groupAuthoritiesByUsername(groupAuthoritiesByUsername())
                .passwordEncoder(passwordEncoder());
//             .withUser(User.withUsername(DB_USER.name())
//                        .password(passwordEncoder().encode(PASSWORD))
//                        .roles(DB_USER.name())
//                        .authorities(listsTo1Array(DB_USER.getRoles()))
        auth.inMemoryAuthentication()
                .withUser(SWAGGER.name())
                .password(passwordEncoder().encode(PASSWORD))
                .roles(listsTo1Array(SWAGGER.getRoles()))
//                .and()
//                .withUser(DB_USER.name())
//                .password(passwordEncoder().encode(PASSWORD))
//                .roles(listsTo1Array(DB_USER.getRoles()))
                .and()
                .withUser(BASIC.name())
                .password(passwordEncoder().encode(PASSWORD))
                .roles(listsTo1Array(BASIC.getRoles()))
                .and()
                .withUser(ADMIN.name())
                .password(passwordEncoder().encode(PASSWORD))
                .roles(listsTo1Array(ADMIN.getRoles()));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private String getUserQuery() {
        return "select username as username, password as password, enabled as enabled "
                + "from users "
                + "where username = ? ";
    }

    private String getAuthoritiesQuery() {
        return "select username, authority from authorities where username=?";
//   or extend (other option to: groupAuthoritiesByUsername)
//     "SELECT * FROM AUTHORITIES WHERE AUTHORITY_ID IN( "+
//                "SELECT DISTINCT AUTHORITY_ID FROM ROLES_AUTHORITIES  S1 "+
//                "JOIN USERS_ROLES S2 ON S1.ROLE_ID = S2.ROLE_ID "+
//                "JOIN USERS S3 ON S3.USER_ID = S2.USER_ID AND S3.USERNAME=?)";
    }

    private String groupAuthoritiesByUsername() {
        return "select g.id, g.group_name, ga.authority " +
                "from groups g, group_members gm, group_authorities ga " +
                "where gm.username = ? and g.id = ga.group_id and g.id = gm.group_id";
    }

    @Configuration
    @Order(1)
    public static class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .exceptionHandling()
                    .and()
                    .antMatcher(API_PATH + "/**").authorizeRequests()
                    .antMatchers(BASIC_AUTH_LIST).hasRole(BASIC.name())
                    .antMatchers(APP_AUTH_LIST).hasRole(ADMIN.name())
                    .antMatchers(NO_AUTH_LIST).permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .httpBasic();
        }

    }

    @Configuration
    @Order(2)
    public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
        private final DataSource dataSource;

        public FormLoginWebSecurityConfigurerAdapter(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .exceptionHandling()
//                    .authenticationEntryPoint(appBasicAuthenticationEntryPoint())
                    .and()
                    .authorizeRequests()
                    .antMatchers(mergeStringArrays(SWAGGER_AUTH_LIST)).hasRole(SWAGGER.name())
                    .antMatchers(NO_AUTH_LIST).permitAll() // or hasAnyRole
                    .anyRequest().authenticated()
                    .and()
                    .formLogin()
                    .successHandler(appAuthenticationSuccessHandler())
                    .failureHandler(appAuthenticationFailureHandler())
                    .and()
                    .rememberMe().key(REMEMBER_ME_KEY)
                    .rememberMeCookieName(REMEMBER_ME_COOKIE)
                    .tokenValiditySeconds(TOKEN_VALIDITY_SECONDS)
                    .tokenRepository(tokenRepository())
                    .and()
                    //todo String to static final
                    .logout().logoutUrl("/logout");
//                 .deleteCookies("JSESSIONID");
//                 .loginPage("/login")
//                 .failureUrl("/login-error")
//                 .loginProcessingUrl("/security_check")
//                 .usernameParameter("username").passwordParameter("password")
//                 .permitAll();
        }

        @Bean
        public AuthenticationSuccessHandler appAuthenticationSuccessHandler() {
            return new PreviousPageAuthenticationSuccessHandler();
        }

        @Bean
        public AuthenticationFailureHandler appAuthenticationFailureHandler() {
            return new AppAuthenticationFailureHandler();
        }

        @Bean
        public JdbcTokenRepositoryImpl tokenRepository() {
            JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
            tokenRepository.setCreateTableOnStartup(false);
            tokenRepository.setDataSource(dataSource);
            return tokenRepository;
        }

    }
}