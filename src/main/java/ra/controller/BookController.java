package ra.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import ra.dto.response.BookResponse;
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
            Book result = bookServiceImp.getById(bookId);
            result.setBookName(book.getBookName());
            result.setDescriptions(book.getDescriptions());
            result.setBookTitle(book.getBookTitle());
            result.setIsbn(book.getIsbn());
            result.setEditionLanguage(book.getEditionLanguage());
            result.setDatePublished(book.getDatePublished());
            result.setPublisher(book.getPublisher());
            result.setImportPrice(book.getImportPrice());
            result.setExportPrice(book.getExportPrice());
            result.setQuantity(book.getQuantity());
            result.setSale(book.getSale());
            result.setBookStatus(book.isBookStatus());
            result.setBookStatus(true);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ok", "Cập nhật thành công book với id là " + bookId, result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ResponseObject("false", "không thể cập nhật book với id là  " + bookId, ""));
        }
    }

    @DeleteMapping("/{bookId}")
    ResponseEntity<ResponseObject> deleteById(@PathVariable int bookId) {
        try {
            bookServiceImp.deleteById(bookId);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ok", "Xóa thành công book  với id là " + bookId, ""));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ResponseObject("false", "không thể xóa  book với id là " + bookId, ""));
        }
    }

    @GetMapping("/search")

    public List<Book> searchName(@RequestParam("bookName") String bookName) {
        return bookServiceImp.searchName(bookName);
    }

    @GetMapping("/sortByName")
    public ResponseEntity<List<Book>> sortBookByName(@RequestParam("direction") String direction) {
        List<Book> listBook = bookServiceImp.sortByName(direction);
        return new ResponseEntity<>(listBook, HttpStatus.OK);
    }

    @GetMapping("/getPagging")
    public ResponseEntity<Map<String, Object>> getPagging(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> pageBook = bookServiceImp.getPagging(pageable);
        Map<String, Object> data = new HashMap<>();
        data.put("catalog", pageBook.getContent());
        data.put("total", pageBook.getSize());
        data.put("totalItems", pageBook.getTotalElements());
        data.put("totalPages", pageBook.getTotalPages());
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/get_best_star")
    public ResponseEntity<?> getBestStar() {
        List<Book> bookList = bookServiceImp.getAll();
        float max= 0;
        BookResponse bookResponse= new BookResponse();
        for (Book book : bookList) {
            BookResponse response=bookServiceImp.mapBookToBookResponse(book);
           if (response.getAvgStar()>max){
               max=response.getAvgStar();
               bookResponse=response;
           }
        }
        return new ResponseEntity<>(bookResponse,HttpStatus.OK);
        }
}
