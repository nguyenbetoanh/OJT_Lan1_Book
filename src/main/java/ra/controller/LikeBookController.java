package ra.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ra.dto.response.BookResponse;
import ra.model.entity.Book;
import ra.model.entity.LikeBook;
import ra.model.entity.Users;
import ra.model.service.BookService;
import ra.model.service.LikeBookService;
import ra.model.service.UserService;
import ra.security.CustomUserDetails;

import java.util.List;

@RestController
@CrossOrigin("http://localhost:8080")
@RequestMapping("/api/v1/likeBook")
@AllArgsConstructor
public class LikeBookController {
    private LikeBookService likeBookService;
    private UserService userService;
    private BookService bookService;

    @PostMapping()
    public ResponseEntity<?> clickLikeBook(@RequestBody Book book) {
        CustomUserDetails usersChangePass = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Users users = userService.findUsersByUserName(usersChangePass.getUsername());
        LikeBook likeBookCheckExist = likeBookService.findByBook_BookIdAndUsers_UserId(book.getBookId(), users.getUserId());
        if (likeBookCheckExist == null) {
            LikeBook likeBook = new LikeBook();
            likeBook.setBook(book);
            likeBook.setUsers(users);
            likeBook.setLikeBookStatus(true);
            likeBookService.saveOrUpdate(likeBook);
            return ResponseEntity.ok().body("Like");
        } else {
            if (likeBookCheckExist.isLikeBookStatus()) {
                likeBookCheckExist.setLikeBookStatus(false);
                likeBookService.saveOrUpdate(likeBookCheckExist);
                return ResponseEntity.ok().body("Dislike");
            } else {
                likeBookCheckExist.setLikeBookStatus(true);
                likeBookService.saveOrUpdate(likeBookCheckExist);
                return ResponseEntity.ok().body("Like");
            }
        }
    }

    @GetMapping("/best_like")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> countLike() {
        try {
            List<Book> bookList= bookService.getAll();
            int maxLike= 0;
            BookResponse response= new BookResponse();
            for (Book book:bookList) {
                if (book.getListLikeBook().size()>maxLike){
                    maxLike=book.getListLikeBook().size();
                    response= bookService.mapBookToBookResponse(book);
                }
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>( HttpStatus.BAD_REQUEST);
        }
    }
}
