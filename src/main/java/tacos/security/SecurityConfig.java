package tacos.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import tacos.User;
import tacos.data.UserDetailsService;
import tacos.data.UserRepository;

@Configuration
public class SecurityConfig {

    /**
     * bean компонент PasswordEncoder, который мы будем использовать при создании новых пользователей и при аутентификации.
     * В данном случае класс использует BCryptPasswordEncoder, один из нескольких средств шифрования паролей
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * UserDetailsService делегирует выполнение операций репозиторию UserRepository
     * принимает параметр UserRepository. Чтобы создать bean-компонент, он возвращает лямбда-функцию,
     * которая принимает параметр username и использует его для вызова метода findByUsername() репозитория UserRepository
     */
    @Bean
    public UserDetailsService userdetailsService(UserRepository userRepo) {
        return username -> {
            User user = userRepo.findByUsername(username);
            if (user != null) return user;
            throw new UsernameNotFoundException("User '" + username + "' not found");
        };

    }

    /**
     * Мы должны убедиться, что запросы с путями /design и /orders будут обрабатываться, только если они отправлены аутентифицированными
     * пользователями; все другие запросы должны обрабатываться независимо от факта аутентификации. Именно это обеспечивает конфигурация
     * Мы должны убедиться, что запросы с путями /design и /orders будут обрабатываться, только если они отправлены аутентифицированными
     * пользователями; все другие запросы должны обрабатываться независимо от факта аутентификации. Именно это обеспечивает следующая конфигурация
     * все остальные запросы должны обрабатываться безоговорочно.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeRequests()
                .antMatchers("/design", "/orders").hasRole("USER")
                .antMatchers("/", "/**").permitAll()
                .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/design") // если пользователь напрямую открыл страницу входа и успешно прошел аутентификацию, то он будет перенаправлен на страницу /design.
                .and()
                .oauth2Login()
                .loginPage("/login")
                .and()
                .logout()
                .and()
                .csrf()
                .ignoringAntMatchers("/h2-console/**")
                .and()
                .headers()
                .frameOptions()
                .sameOrigin()
                .and()
                .build();

    }

}
