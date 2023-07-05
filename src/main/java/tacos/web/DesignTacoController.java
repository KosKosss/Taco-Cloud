package tacos.web;

import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import tacos.Ingredient;
import tacos.Ingredient.Type;
import tacos.Taco;
import tacos.TacoOrder;
import tacos.data.IngredientRepository;
import tacos.data.TacoRepository;


import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Контроллер выполняет следующие действия:
 * принимает и обрабатывает HTTP-запросы GET с путем /design;
 * составляет список ингредиентов;
 * передает запрос и ингредиенты в шаблон представления, который будет преобразован в HTML и отправлен веб-бр
 */


@Slf4j //во время компиляции генерирует Logger
@Controller // идентифицирует этот класс как контроллер и отмечает его как доступный для механизма сканирования компонентов, чтобы фреймворк мог обнаружить его и  автоматически создать экземпляр DesignTacoController в виде bean-компонента в контексте приложения.
@RequestMapping("/design") //определяет тип запросов, в данном случае будет обрабатывать запросы пути в которых начинаются с /design
@SessionAttributes("tacoOrder") //указывает что объект ТакоОрдер должен поддерживаться на уровне сеанса
public class DesignTacoController {

    private final IngredientRepository ingredientRepo;
    private TacoRepository tacoRepo;
    @Autowired
    public DesignTacoController(IngredientRepository ingredientRepo, TacoRepository tacoRepo){
        this.ingredientRepo = ingredientRepo;
        this.tacoRepo = tacoRepo;
    }


    @ModelAttribute //метод будет вызываться в процессе обработки запроса и создавать список объектов Ingredient, который затем будет помещен в модель.
    public void addIngredientsToModel(Model model) {

        List<Ingredient> ingredients = new ArrayList<>();
                ingredientRepo.findAll().forEach(i -> ingredients.add(i));

        Type[] types = Ingredient.Type.values();
        for (Type type : types) {
            model.addAttribute(type.toString().toLowerCase(),
                    filterByType(ingredients, type));
        }
    }

    @ModelAttribute(name = "tacoOrder") // метод просто создает новые объекты TacoOrder для размещения в модели
    public TacoOrder order() {
        return new TacoOrder();
    }

    @ModelAttribute(name = "taco") // метод просто создает новые объекты Taco для размещения в Модели
    public Taco taco() {
        return new Taco();
    }

    @GetMapping // определяет метод , который должен вызываться для обработки HTTP-запроса GET с путем /design.
    public String showDesignForm(){
        return "design";
    }


    //Аннотация @PostMapping, сообщает аннотации @RequestMapping на уровне класса, что processTaco() будет обрабатывать запросы POST с путем /design.
    //Аннотация @Valid требует выполнить проверку отправленного
    //объекта Taco после его привязки к данным в отправленной форме, но
    //до начала выполнения тела метода processTaco(). Если обнаружатся
    //какие-либо ошибки, то сведения о них будут зафиксированы в объекте Errors, который передается в processTaco().
    @PostMapping
    public String processTaco(@Valid Taco taco, Errors errors,
                              @ModelAttribute TacoOrder tacoOrder){
        if(errors.hasErrors()){
            return "design";
        }
        Taco saved = tacoRepo.save(taco);
        tacoOrder.addTaco(saved);

        return "redirect:/orders/current";
    }

    private List<Ingredient> filterByType(List<Ingredient> ingredients, Type type){
        return ingredients.stream()
                .filter(x ->x.getType()
                .equals(type))
                .collect(Collectors.toList());
    }
}
