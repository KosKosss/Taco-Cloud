package tacos.web.api;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import tacos.Ingredient;
import tacos.User;

import tacos.data.IngredientRepository;

import java.security.Principal;

@RestController
@RequestMapping(path="/api/ingredients", produces="application/json")
@CrossOrigin(origins="http://localehost:8080")
public class IngredientController {

    private IngredientRepository ingredientRepo;

    //чтобы автоматическое связывание определялось явно, можно к конструктору добавить аннотацию @Autowired
    @Autowired
    public IngredientController(IngredientRepository ingredientRepo) {
        this.ingredientRepo = ingredientRepo;
    }

    @GetMapping
    public Iterable<Ingredient> allIngredients(){

        return ingredientRepo.findAll();
    }

    /**
     * @RequestBody, указывающей, что тело запроса должно быть преобразовано в объект Ingredient
     * и присвоено параметру.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("#{hasRole(‘ADMIN’)}")
    public Ingredient saveIngredient(@RequestBody Ingredient ingredient){
        return  ingredientRepo.save(ingredient);
    }


    /**
     * Поскольку контроллер имеет базовый путь /api/ingredients, этот метод
     * обрабатывает GET-запросы с путями /api/ingredients/{id}, где часть пути {id} –
     * это переменная-заполнитель. Фактическое значение в запросе присваивается параметру id, которое извлекается из заполнителя {id}
     * с помощью @PathVariable.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("#{hasRole(‘ADMIN’)}")
    public void deleteIngredient(@PathVariable("id") String ingredientId){
        ingredientRepo.deleteById(ingredientId);
    }
}
