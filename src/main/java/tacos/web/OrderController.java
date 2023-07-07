package tacos.web;

import javax.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import tacos.TacoOrder;
import tacos.User;
import tacos.data.OrderRepository;

/** после составления заказа и нажатии на кнопку Submit Your Taco
 * идет перенаправление методом processTaco на форму заказа,
 * откуда он сможет сделать заказ на доставку своего заказа
 */

/**
 * контроллер будет обрабатывать запросы с путем /orders/current
 */


//@Slf4j // автоматически создает объект Logger во время компиляции. Мы используем его, чтобы зарегистрировать в журнале детали отправленного заказа
@Controller
@RequestMapping("/orders") //любые методы обработки запросов в этом контроллере будут обрабатывать запросы с путями, начинающимися с /orders
@SessionAttributes("tacoOrder")
public class OrderController {

    private OrderRepository orderRepo;

    public OrderController(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    //метод будет обрабатывать HTTP-запросы GET с путем /orders/current
    @GetMapping("/current")
    public String orderForm(@AuthenticationPrincipal User user,
                            @ModelAttribute TacoOrder order){
        if (order.getDeliveryName() == null) {
            order.setDeliveryName(user.getFullname());
        }
        if (order.getDeliveryStreet() == null) {
            order.setDeliveryStreet(user.getStreet());
        }
        if (order.getDeliveryCity() == null) {
            order.setDeliveryCity(user.getCity());
        }
        if (order.getDeliveryState() == null) {
            order.setDeliveryState(user.getState());
        }
        if (order.getDeliveryZip() == null) {
            order.setDeliveryZip(user.getZip());
        }
        return "orderForm";
    }


    // Когда приложение вызовет метод processOrder() для обработки отправленного заказа,
    // ему будет передан объект TacoOrder со значениями свойств, полученными из полей отправленной формы.

    //Аннотация @Valid требует выполнить проверку отправленного
    //объекта TacoOrder после его привязки к данным в отправленной форме, но
    //до начала выполнения тела метода processOrder(). Если обнаружатся
    //какие-либо ошибки, то сведения о них будут зафиксированы в объекте Errors, который передается в processTaco().
    @PostMapping
    public String processOrder(@Valid TacoOrder order, Errors errors, SessionStatus sessionStatus, @AuthenticationPrincipal User user){

        if(errors.hasErrors()){
            return "orderForm";
        }
        order.setUser(user);
        orderRepo.save(order);
        sessionStatus.setComplete();
        return "redirect:/";
    }
}