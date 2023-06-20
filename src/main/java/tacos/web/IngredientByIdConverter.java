package tacos.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import tacos.Ingredient;
import tacos.data.IngredientRepository;

import java.util.HashMap;
import java.util.Map;


/** используется для преобразования Стринг в Ингредиент
 * конструктор IngredientByIdConverter создает экземпляр Map с ключами типа String,
 * которые служат идентификаторами ингредиентов, и значениями типа Ingredient,
 * представляющими сами объекты
 */

@Component
public class IngredientByIdConverter implements Converter<String, Ingredient> {

    private IngredientRepository ingredientRepo;

    @Autowired
    public IngredientByIdConverter(IngredientRepository ingredientRepo){
        this.ingredientRepo = ingredientRepo;
    }


    // прнимает строку, служащую идентификатором ингредиента, и использует ее для поиска соответствующего ингредиента в ассоциативном массиве Map
    @Override
    public Ingredient convert(String id){
        return ingredientRepo.findById(id).orElse(null);
    }
}
