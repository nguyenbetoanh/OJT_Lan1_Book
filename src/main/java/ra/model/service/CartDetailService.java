package ra.model.service;

import org.springframework.data.domain.Page;
import ra.model.entity.CartDetail;
import ra.model.entity.Carts;

import java.util.List;

public interface CartDetailService extends RootService<CartDetail,Integer> {
    CartDetail findByBook_BookIdAndCarts_CartId(Integer bookId,Integer cartId);
    boolean deleteByCartDetailId(Integer id);
    List<CartDetail> findByBook_BookId(int bookId);
    List<CartDetail> findByCartsIn(List<Carts> listCart);


}
