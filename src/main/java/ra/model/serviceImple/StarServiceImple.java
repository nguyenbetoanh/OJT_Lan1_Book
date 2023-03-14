package ra.model.serviceImple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ra.dto.request.StarRequest;
import ra.model.entity.Book;
import ra.model.entity.Star;
import ra.model.entity.Users;
import ra.model.repository.StarRepository;
import ra.model.service.BookService;
import ra.model.service.StarService;
import ra.model.service.UserService;

import java.util.List;

@Service
public class StarServiceImple implements StarService {
    @Autowired private StarRepository repository;
    @Autowired private UserService userService;
    @Autowired private BookService bookService;
    @Override
    public Page<Star> getAllList(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Star saveOrUpdate(Star star) {
        return repository.save(star);
    }

    @Override
    public Star findById(Integer id) {
        return repository.findById(id).get();
    }

    @Override
    public Page<Star> findByName(String name, Pageable pageable) {
        return null;
    }

    @Override
    public List<Star> findAll() {
        return repository.findAll();
    }

    @Override
    public Star mapRequestToStar(Integer userId,StarRequest request) {
        Book book= bookService.getById(request.getBookId());
        Users users=userService.findById(userId);
        Star star = new Star();
        star.setStarPoint(request.getStarPoint());
        star.setUsers(users);
        star.setBook(book);
        return star;
    }

    @Override
    public Star findByBookIdAndUserId(Integer bookId, Integer userId) {
        return repository.findByBook_BookIdAndUsers_UserId(bookId,userId);
    }
}
