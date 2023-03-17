package ra.model.serviceImple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ra.dto.response.BookResponse;
import ra.model.entity.Book;
import ra.model.entity.CartDetail;
import ra.model.entity.Star;
import ra.model.repository.BookRepository;
import ra.model.service.BookService;

import java.util.List;

@Service
public class BookServiceImp implements BookService {
    @Autowired
    private BookRepository bookRepository;

    @Override
    public List<Book> getAll() {
        return bookRepository.findAll();
    }

    @Override
    public Book getById(int bookId) {
        return bookRepository.findById(bookId).get();
    }

    @Override
    public void deleteById(int bookId) {
        bookRepository.deleteById(bookId);

    }

    @Override
    public Book saveOrUpdate(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public Page<Book> searchName(String bookName,Pageable pageable) {
        return bookRepository.findByBookNameContaining(bookName,pageable);
    }

    @Override
    public List<Book> sortByName(String direction) {
        if (direction.equals("asc")) {
            return bookRepository.findAll(Sort.by("bookName").ascending());
        } else {
            return bookRepository.findAll(Sort.by("bookName").descending());
        }
    }

    @Override
    public Page<Book> getPagging(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    @Override
    public List<Book> getAllWishList(int userId) {
        return bookRepository.getAllWishList(userId);
    }

    @Override
    public List<Book> findByCartDetailsIn(List<CartDetail> listCartDetail) {
        return bookRepository.findByCartDetailsIn(listCartDetail);
    }

    @Override
    public BookResponse mapBookToBookResponse(Book book) {
        BookResponse response = new BookResponse();
        response.setBookId(book.getBookId());
        response.setBookName(book.getBookName());
        response.setBookStatus(book.isBookStatus());
        response.setBookTitle(book.getBookTitle());
        response.setDatePublished(book.getDatePublished());
        response.setDescriptions(book.getDescriptions());
        response.setEditionLanguage(book.getEditionLanguage());
        response.setExportPrice(book.getExportPrice());
        response.setImportPrice(book.getImportPrice());
        response.setIsbn(book.getIsbn());
        response.setPublisher(book.getPublisher());
        response.setQuantity(book.getQuantity());
        response.setSale(book.getSale());
        response.setAuthor(book.getAuthor());
        response.setCatalog(book.getCategory());
        response.setTagList(book.getTagList());
        response.setListComment(book.getListComment());
        response.setCountLike(book.getListLikeBook().size());
        if (book.getStarList().size()==0){
            response.setAvgStar(0);
        }else {
            List<Star> starList = book.getStarList();
            int sum = 0;
            for (Star star : starList) {
                sum+= star.getStarPoint();
            }
            response.setAvgStar(sum/book.getStarList().size());
        }
        return response;
    }

    @Override
    public List<Book> findByCategory_CatalogId(Integer catId) {
        return bookRepository.findByCategory_CatalogId(catId);
    }
}
