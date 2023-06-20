package tacos.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserDetailsService {


    /**
     *  принимает имя пользователя и отыскивает соответствующий объект UserDetails. Если учетная запись
     * с таким именем пользователя не будет найдена, то метод сгенерирует исключение UsernameNotFoundException.
     */
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
