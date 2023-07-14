package tacos.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import tacos.User;
import tacos.data.UserRepository;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter{

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
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/ingredients").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/ingredients/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/api/ingredients")
                .hasAuthority("SCOPE_writeIngredients")
                .antMatchers(HttpMethod.DELETE, "/api//ingredients")
                .hasAuthority("SCOPE_deleteIngredients")
                .and()
                .csrf()
                .ignoringAntMatchers("/h2-console/**")
                .and()
                .headers()
                .frameOptions()
                .sameOrigin()
                .and()
                .oauth2ResourceServer(oauth2 -> oauth2.jwt())
                 // Добавляем эту строку
                .formLogin(); // Пример настройки формы входа (замените на свои нужды)
    }


    /**
     * bean-компонент SecurityFilterChain настраивает Spring
     * Security так, чтобы все запросы требовали аутентификации.
     * компонент SecurityFilterChain также включает поддержку OAuth 2 на стороне клиента. В  частности, он настраивает
     * путь к  странице входа /oauth2/authorization/taco-admin-client.
     * компонент SecurityFilterChain также включает поддержку OAuth 2 на стороне клиента. В  частности, он настраивает
     * путь к  странице входа /oauth2/authorization/taco-admin-client.
     */
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(
                        authorizeRequests -> authorizeRequests.anyRequest().authenticated()
                )
                .oauth2Login(
                        oauth2Login ->
                                oauth2Login.loginPage("/oauth2/authorization/taco-admin-client"))
                .oauth2Client(withDefaults());
        return http.build();
    }



}
