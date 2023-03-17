package ra.model.service;
import ra.dto.request.CartConfirm;
import ra.model.entity.Carts;

import java.time.LocalDate;
import java.util.List;

public interface CartService  extends RootService<Carts,Integer>{
    Carts mapCartConfirmToCart(Carts carts, CartConfirm confirm);

    List<Carts> findByCreatDateBetween(LocalDate startDate, LocalDate endDate);
    List<Carts>findByUsers_UserIdAndCartStatus(Integer userId,Integer cartStatus);
}
