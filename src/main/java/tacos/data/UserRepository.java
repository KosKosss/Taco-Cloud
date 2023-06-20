package tacos.data;

import org.springframework.data.repository.CrudRepository;
import tacos.User;

public interface UserRepository extends CrudRepository<User, Long> {
    /**
     * В дополнение к операциям создания/чтения/изменения/удаления (CRUD),
     * которые поддерживает CrudRepository, интерфейс UserRepository определяет метод findByUsername(),
     * который мы будем использовать для поиска учетной записи по имени пользователя.
     */
    User findByUsername(String username);
}
