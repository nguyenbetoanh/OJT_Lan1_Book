package ra.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ra.model.entity.Book;
import ra.model.entity.Category;
import ra.model.entity.ResponseObject;
import ra.model.service.BookService;
import ra.model.serviceImple.CategoryServiceImp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@CrossOrigin("http://localhost:8080")
@RequestMapping("api/v1/category")
@AllArgsConstructor
public class CategoryController {
    private CategoryServiceImp categoryServiceImp;
    private BookService bookService;

    @GetMapping
    public List<Category> getAll() {
        return categoryServiceImp.getAll();
    }

    @GetMapping("/{catalogId}")
    ResponseEntity<ResponseObject> findById(@PathVariable int catalogId) {
        try {
            Category foundStudent = categoryServiceImp.getById(catalogId);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ok", "Tìm thành công category", foundStudent));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("false", "không thể tìm thấy category có id là " + catalogId, ""));
        }
    }

    @PostMapping
    ResponseEntity<ResponseObject> createCategory(@RequestBody Category category) {
        try {
            category.setCatalogStatus(true);
            Category categoryNew = categoryServiceImp.saveOrUpdate(category);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ok", "Thêm thành công category", categoryNew));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ResponseObject("false", "không thể thêm category  ", ""));
        }
    }

    @PutMapping("/{catalogId}")
    ResponseEntity<ResponseObject> saveOrUpdate(@RequestBody Category category, @PathVariable int catalogId) {
        try {
            Category categoryUpdate = categoryServiceImp.getById(catalogId);
            categoryUpdate.setCatalogName(category.getCatalogName());
            categoryUpdate.setCatalogStatus(category.isCatalogStatus());
            Category cat = categoryServiceImp.saveOrUpdate(categoryUpdate);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ok", "Cập nhật thành công category với id là " + catalogId, cat));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ResponseObject("false", "không thể cập nhật category với id là  " + catalogId, ""));
        }
    }

    @DeleteMapping("/{catalogId}")
    ResponseEntity<ResponseObject> deleteById(@PathVariable int catalogId) {
        try {
            Category category = categoryServiceImp.getById(catalogId);
            List<Book> bookList = bookService.findByCategory_CatalogId(catalogId);
            for (Book book : bookList) {
                book.setBookStatus(false);
                bookService.saveOrUpdate(book);
            }
            category.setCatalogStatus(false);
            categoryServiceImp.saveOrUpdate(category);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ok", "Xóa thành công category  với id là " + catalogId, ""));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ResponseObject("false", "không thể xóa  category với id là " + catalogId, ""));
        }
    }

        @GetMapping("/search")

    public List<Category> searchNameOrId(@RequestParam("catalogName") String catalogName) {
        return categoryServiceImp.searchName(catalogName);
    }


    @GetMapping("/sortByName")
    public ResponseEntity<List<Category>> sortStudentByName(@RequestParam("direction") String direction) {
        List<Category> listStudent = categoryServiceImp.sortByName(direction);
        return new ResponseEntity<>(listStudent, HttpStatus.OK);
    }

    @GetMapping("/getPagging")
    public ResponseEntity<Map<String, Object>> getPagging(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> pageStudent = categoryServiceImp.getPagging(pageable);
        Map<String, Object> data = new HashMap<>();
        data.put("catalog", pageStudent.getContent());
        data.put("total", pageStudent.getSize());
        data.put("totalItems", pageStudent.getTotalElements());
        data.put("totalPages", pageStudent.getTotalPages());
        return new ResponseEntity<>(data, HttpStatus.OK);
    }
}
