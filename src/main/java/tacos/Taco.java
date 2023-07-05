package tacos;


import javax.persistence.*;
import javax.validation.constraints.Size;
import lombok.Data;
import javax.validation.constraints.NotNull;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class Taco {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Date createdAt = new Date();

    @NotNull //имя не должно быть пустым и имень длинну минимум 5 символов
    @Size(min = 5, message = "Name must be at least 5 characters long")
    private String name;


    @Size(min = 1, message = "You must choose at least 1 ingredient")
    @ManyToMany(targetEntity = Ingredient.class)
    //Объект Taco может включать в список несколько объектов Ingredient, а  один объект Ingredient может быть частью списков в  нескольких объектах Taco.
    private List<Ingredient> ingredients;
}
