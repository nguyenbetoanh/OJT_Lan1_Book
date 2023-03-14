package ra.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import ra.dto.response.BookResponse;
import ra.model.entity.Author;
import ra.model.entity.Book;
import ra.model.entity.ResponseObject;

import ra.model.serviceImple.BookServiceImp;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("http://localhost:8080")
@RequestMapping("/api/v1/book")
@AllArgsConstructor
public class BookController {
    @Autowired
    private BookServiceImp bookServiceImp;

    @GetMapping
    public List<Book> getAll() {
        return bookServiceImp.getAll();
    }

    @GetMapping("/{bookId}")
    ResponseEntity<ResponseObject> findById(@PathVariable int bookId) {
        try {
            Book result = bookServiceImp.getById(bookId);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ok", "Tìm thành công Book", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("false", "không thể tìm thấy Book có id là " + bookId, ""));
        }
    }

    @PostMapping
    ResponseEntity<ResponseObject> createBook(@RequestBody Book book) {
        try {
            Book result = bookServiceImp.saveOrUpdate(book);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ok", "Thêm thành công book", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ResponseObject("false", "không thể thêm book  ", ""));
        }
    }

    @PutMapping("/{bookId}")
    ResponseEntity<ResponseObject> saveOrUpdate(@RequestBody Book book, @PathVariable int bookId) {
        try {
            Book bookUpdate = bookServiceImp.getById(bookId);
            bookUpdate.setBookName(book.getBookName());
            bookUpdate.setDescriptions(book.getDescriptions());
            bookUpdate.setBookTitle(book.getBookTitle());
            bookUpdate.setIsbn(book.getIsbn());
            bookUpdate.setEditionLanguage(book.getEditionLanguage());
            bookUpdate.setDatePublished(book.getDatePublished());
            bookUpdate.setPublisher(book.getPublisher());
            bookUpdate.setImportPrice(book.getImportPrice());
            bookUpdate.setExportPrice(book.getExportPrice());
            bookUpdate.setQuantity(book.getQuantity());
            bookUpdate.setSale(book.getSale());
            bookUpdate.setBookStatus(book.isBookStatus());
            bookUpdate.setBookStatus(true);
            Book save = bookServiceImp.saveOrUpdate(bookUpdate);
            BookResponse result = bookServiceImp.mapBookToBookResponse(save);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ok", "Cập nhật thành công book với id là " + bookId, result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ResponseObject("false", "không thể cập nhật book với id là  " + bookId, ""));
        }
    }

    @DeleteMapping("/{bookId}")
    ResponseEntity<ResponseObject> deleteById(@PathVariable int bookId) {
        try {
            Book book = bookServiceImp.getById(bookId);
            book.setBookStatus(false);
            Book result = bookServiceImp.saveOrUpdate(book);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ok", "Xóa thành công book  với id là " + bookId, result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ResponseObject("false", "không thể xóa  book với id là " + bookId, ""));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchByName(
            @RequestParam String searchName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {
        Map<String, Object> data = new HashMap<>();
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Book> bookPage = bookServiceImp.searchName(searchName, pageable);
            for (Book book : bookPage) {
            }
            data.put("authors", bookPage.getContent());
            data.put("total", bookPage.getSize());
            data.put("totalAuthors", bookPage.getTotalElements());
            data.put("totalPage", bookPage.getTotalPages());
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get_pagging_and_sortBy")
    public ResponseEntity<Map<String, Object>> getPagging(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam String direction,
            @RequestParam String sortBy) {
        Sort.Order order = null;
        if (direction.equals("asc")) {
            order = new Sort.Order(Sort.Direction.ASC, sortBy);
        } else if (direction.equals("des")) {
            order = new Sort.Order(Sort.Direction.DESC, sortBy);
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(order));
        Page<Book> pageBook = bookServiceImp.getPagging(pageable);
        Map<String, Object> data = new HashMap<>();
        data.put("books", pageBook.getContent());
        data.put("total", pageBook.getSize());
        data.put("totalItems", pageBook.getTotalElements());
        data.put("totalPages", pageBook.getTotalPages());
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/get_best_star")
    public ResponseEntity<?> getBestStar() {
        List<Book> bookList = bookServiceImp.getAll();
        float max = 0;
        BookResponse bookResponse = new BookResponse();
        for (Book book : bookList) {
            BookResponse response = bookServiceImp.mapBookToBookResponse(book);
            if (response.getAvgStar() > max) {
                max = response.getAvgStar();
                bookResponse = response;
            }
        }
        return new ResponseEntity<>(bookResponse, HttpStatus.OK);
    }
}
