package tacos.security;


import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;
import tacos.User;


/**
 * RegistrationForm – это просто класс, снабженный аннотациями Lombok и имеющий несколько свойств.
 * Метод toUser() использует эти свойства для создания нового объекта User, который будет сохраняться processRegistration() в UserRepository
 */

@Data
public class RegistrationForm {
    private final String username;
    private final String password;
    private final String fullname;
    private final String street;
    private final String city;
    private final String state;
    private final String zip;
    private final String phone;

    public User toUser(PasswordEncoder passwordEncoder){
        return new User(username, passwordEncoder.encode(password),
                fullname, street, city, state, zip, phone);
    }
}
