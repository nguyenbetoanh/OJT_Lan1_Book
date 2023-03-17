package ra.model.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ra.model.entity.Book;
import ra.model.entity.CartDetail;

import java.util.List;
@Repository
public interface BookRepository extends JpaRepository<Book,Integer> {
    Page<Book> findByBookNameContaining(String bookName, Pageable pageable);
    @Query(value = "select * from book b join wishlist w on b.bookId = w.bookId where w.userId = :uId",nativeQuery = true)
    List<Book> getAllWishList(@Param("uId") int userId);

    List<Book> findByCartDetailsIn(List<CartDetail> listCartDetail);
    List<Book>findByCategory_CatalogId(Integer catId);
}
