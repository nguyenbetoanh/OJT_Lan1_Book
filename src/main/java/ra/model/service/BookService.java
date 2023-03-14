package ra.model.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ra.dto.response.BookResponse;
import ra.model.entity.Book;
import ra.model.entity.CartDetail;
import ra.model.entity.Category;

import java.util.List;

public interface BookService {
    List<Book> getAll();
    Book getById(int bookId);
    void deleteById(int bookId);
    Book saveOrUpdate(Book book);
    List<Book> searchName(String bookName);
    List<Book> sortByName(String direction);
    Page<Book> getPagging(Pageable pageable);
    List<Book> getAllWishList(int userId);
    List<Book> findByCartDetailsIn(List<CartDetail> listCartDetail);
   BookResponse mapBookToBookResponse(Book book);
    List<Book>findByCategory_CatalogId(Integer catId);
}
