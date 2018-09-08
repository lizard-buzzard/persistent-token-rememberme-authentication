package com.lizardbuzzard.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@PropertySource("classpath:jdbc.properties")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("userDetailsService")
    private UserDetailsService userDetailsService;

    @Autowired
    @Qualifier("authenticationSuccessHandler")
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/app", "/app/accessDenied").permitAll()
                .and().exceptionHandling().accessDeniedPage("/app/accessDenied")
                .and().csrf().disable();

        // two variants of AuthorizedUrl configuration
        http.authorizeRequests()
                .antMatchers("/app/homepage/adminconsole/**").access("hasRole('ROLE_ADMIN')");
        http.authorizeRequests()
                .antMatchers("/app/homepage/user/**", "/app/redirect").hasRole("USER");  // "ROLE_" adds automatically

        //login configuration
        http.formLogin()
                .loginPage("/app")
                .loginProcessingUrl("/loginFormPostTo")
                .usernameParameter("myLoginPageUsernameParameterName")
                .passwordParameter("myLoginPagePasswordParameterName")
//                .defaultSuccessUrl("/app/homepage/admin")
                .successHandler(authenticationSuccessHandler);

        //remember me configuration
        http.rememberMe()
                .tokenRepository(persistentTokenRepository())
                .rememberMeParameter("myRememberMeParameterName")
                .rememberMeCookieName("my-remember-me")
                .tokenValiditySeconds(86400);

        //logout configuration
        http.logout()
                .logoutUrl("/appLogout")
                .logoutSuccessUrl("/app");

        http.sessionManagement().maximumSessions(1);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(getPasswordEncoder());
    }

    @Bean(value = "passwordEncoder")
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder(7);
    }

    @Autowired
    @Qualifier("dataSource")
    DataSource dataSource;

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        MyJdbcTokenRepositoryImpl tokenRepository = new MyJdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }
}  
