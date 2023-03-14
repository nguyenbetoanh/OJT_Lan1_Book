package ra.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ra.dto.request.StarRequest;
import ra.model.entity.*;
import ra.model.service.*;
import ra.security.CustomUserDetails;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin("http://localhost:8080")
@RequestMapping("/api/v1/star")
@AllArgsConstructor
public class StarController {
    private StarService starService;
    private UserService userService;
    private BookService bookService;
    private CartDetailService cartDetailService;
    private CartService cartService;

    @PostMapping("/vote_star")
    public ResponseEntity<?> creatNewStar(@RequestBody StarRequest request) {
        try {
            CustomUserDetails usersChangePass = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Users users = userService.findUsersByUserName(usersChangePass.getUsername());
            Star oldStar = starService.findByBookIdAndUserId(request.getBookId(), users.getUserId());
            if (oldStar != null) {
                oldStar.setStarPoint(request.getStarPoint());
                Star result = starService.saveOrUpdate(oldStar);
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                List<Carts> carts = cartService.findByUsers_UserIdAndCartStatus(users.getUserId(), 1);
                List<CartDetail> listCartDetail = cartDetailService.findByCartsIn(carts);
                boolean check = false;
                for (CartDetail catDt : listCartDetail) {
                    if (catDt.getBook().getBookId()== request.getBookId()){
                        check= true;
                        break;
                    }
                }
                if (check){
                    Star star = starService.mapRequestToStar(users.getUserId(), request);
                    Star result = starService.saveOrUpdate(star);
                    return new ResponseEntity<>(result, HttpStatus.OK);
                }else {
                    return new ResponseEntity<>( "Mua hang truoc khi danh gia",HttpStatus.BAD_REQUEST);
                }
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
