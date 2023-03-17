package ra.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ra.dto.request.BestSellerBook;
import ra.model.entity.Book;
import ra.model.entity.CartDetail;
import ra.model.entity.Carts;
import ra.model.service.BookService;
import ra.model.service.CartDetailService;
import ra.model.service.CartService;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.*;

@RestController
@CrossOrigin("http://localhost:8080")
@RequestMapping("/api/v1/featureBook")
@AllArgsConstructor
public class FeatureBook {
    @Autowired
    private CartService cartService;
    @Autowired
    private CartDetailService cartDetailService;
    @Autowired
    private BookService bookService;

    @GetMapping
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getBestSellerBook(@RequestBody BestSellerBook bestSellerBook) {
        try {
            List<Carts> listCart = cartService.findByCreatDateBetween(bestSellerBook.getStartDate(), bestSellerBook.getEndDate());
            List<CartDetail> listCartDetail = cartDetailService.findByCartsIn(listCart);
            Map<Integer, Integer> maxMap= new HashMap<>();
            for (int i = 0; i < listCartDetail.size()-1; i++) {
                int quantity=listCartDetail.get(i).getQuantity();
                int key= listCartDetail.get(i).getBook().getBookId();
                if (maxMap.containsKey(key)){
                    int value= maxMap.get(key);
                    maxMap.put(key,value+quantity);
                }else {
                    maxMap.put(key,quantity);
                }
            }
            int maxValue =0;
            int bookId=0;
            for (Map.Entry<Integer, Integer> entry : maxMap.entrySet()) {
                int value = entry.getValue();
                if (value > maxValue) {
                    maxValue = value;
                    bookId= entry.getKey();
                }
            }
            Book bestSale= bookService.getById(bookId);
            return  new ResponseEntity<>(bestSale, HttpStatus.OK);
        }catch (Exception e){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }
}
