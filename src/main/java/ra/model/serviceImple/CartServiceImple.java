package ra.model.serviceImple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ra.dto.request.CartConfirm;
import ra.model.entity.Carts;
import ra.model.entity.Users;
import ra.model.repository.CartRepository;
import ra.model.service.CartService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
@Service
public class CartServiceImple implements CartService {
    @Autowired private CartRepository cartRepository;
    @Override
    public Page<Carts> getAllList(Pageable pageable) {
        List<Integer> status=new ArrayList<>();
        status.add(0);
        Page<Carts> carts=cartRepository.findByCartStatusNotIn(status,pageable);
        return carts;
    }
    @Override
    public Carts saveOrUpdate(Carts carts) {
        return cartRepository.save(carts);
    }
    @Override
    public Carts findById(Integer id) {
        return cartRepository.findById(id).get();
    }
    @Override
    public Page<Carts> findByName(String name, Pageable pageable) {
        return cartRepository.findByCartNameContaining(name, pageable);
    }
    @Override
    public List<Carts> findAll() {
        return cartRepository.findAll();
    }

    @Override
    public Carts mapCartConfirmToCart(Carts cart, CartConfirm confirm) {
        DateFormat df= new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        String txTrack= "TAT-JV08" + df.format(date) + "-" + (int) ((Math.random()) * 1000);
        cart.setCartName(txTrack);
        cart.setAddress(confirm.getAddress());
        cart.setCartStatus(1);
        cart.setCity(confirm.getCity());
        cart.setCreatDate(LocalDate.now());
        cart.setDiscount(confirm.getDiscount());
        cart.setEmail(confirm.getEmail());
        cart.setFirstName(confirm.getFirstName());
        cart.setLastName(confirm.getLastName());
        cart.setNote(confirm.getNote());
        cart.setPhone(confirm.getPhone());
        cart.setPostCode(confirm.getPostCode());
        cart.setState(confirm.getState());
        return cart;
    }

    @Override
    public List<Carts> findByCreatDateBetween(LocalDate startDate, LocalDate endDate) {
        return cartRepository.findByCreatDateBetween(startDate, endDate);
    }

    @Override
    public List<Carts> findByUsers_UserIdAndCartStatus(Integer userId,Integer cartStatus) {
        return cartRepository.findByUsers_UserIdAndCartStatus(userId,cartStatus);
    }
}
