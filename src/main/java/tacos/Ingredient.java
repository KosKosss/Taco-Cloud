package tacos;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;


@Data
//@AllArgsConstructor // упростить cоздание объекта Ingredient со всеми инициализированными свойствами
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true) //JPA требует, чтобы сущности имели конструктор без аргументов, и  аннотация @NoArgsConstructor из библиотеки Lombok автоматически создает такой конструктор.
@Entity
@RequiredArgsConstructor //добавление аннотации @RequiredArgsConstructor гарантирует, что мы по-прежнему будем иметь конструктор со всеми обязательными аргументами, помимо приватного конструктора без аргументов.

public class Ingredient {

    @Id
    private final String id;
    private final String name;
    @Enumerated(EnumType.STRING)
    private final Type type;

    public enum Type{
        WRAP, PROTEIN, VEGGIES, CHEESE, SAUCE
    }
}
