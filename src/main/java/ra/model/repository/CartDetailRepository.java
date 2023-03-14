package ra.model.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ra.model.entity.Book;
import ra.model.entity.CartDetail;
import ra.model.entity.Carts;
import ra.model.entity.Comment;

import java.util.List;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail,Integer> {
    CartDetail findByBook_BookIdAndCarts_CartId(int bookId,int cartId);
    List<CartDetail> findByBook_BookId(int bookId);
    List<CartDetail> findByCartsIn(List<Carts> listCart);



}
