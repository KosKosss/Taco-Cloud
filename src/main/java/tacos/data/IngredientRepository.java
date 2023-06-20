package tacos.data;

import org.springframework.data.repository.CrudRepository;
import tacos.Ingredient;


/**
 * получение всех ингредиентов в виде коллекции объектов Ingredient;
 * получение одного ингредиента по идентификатору;
 * сохранение объекта Ingredient
 */

public interface IngredientRepository extends CrudRepository<Ingredient, String> {

}
