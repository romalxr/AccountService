package account.security;

import account.handler.MyAccessDeniedHandler;
import account.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.web.AuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    //@Lazy
    EventService eventService;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers("/actuator/**").permitAll()
                .mvcMatchers("/h2-console/**").permitAll()
                .mvcMatchers("/api/auth/signup").permitAll()
                .mvcMatchers("/api/empl/payment").hasAnyAuthority("USER", "ACCOUNTANT")
                .mvcMatchers("/api/acct/payments").hasAuthority("ACCOUNTANT")
                .mvcMatchers("/api/security/events").hasAuthority("AUDITOR")
                .mvcMatchers("/api/admin/user/**").hasAuthority("ADMINISTRATOR")
                .mvcMatchers("/**").authenticated() // or .anyRequest().authenticated()
                .and()
                    .httpBasic()
                    .authenticationEntryPoint(getEntryPoint())
                .and()
                    .csrf(AbstractHttpConfigurer::disable)
                    .headers((headers) -> headers.frameOptions().sameOrigin())
                    .formLogin()
                .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // no session
                .and()
                    .exceptionHandling()
                    .accessDeniedHandler(new MyAccessDeniedHandler(eventService));
    }

    @Bean
    public AuthenticationEntryPoint getEntryPoint() {
        return new MyAuthenticationEntryPoint();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService) // user store 1
                .passwordEncoder(getEncoder());
    }

    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }

}
