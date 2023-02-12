package account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsService userDetailsService;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers("api/empl/payment", "api/auth/changepass").authenticated()
                .mvcMatchers("api/auth/signup", "api/acct/payments", "/**").permitAll() // or .anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                .httpBasic(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .headers((headers) -> headers.frameOptions().sameOrigin())
        ;
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

