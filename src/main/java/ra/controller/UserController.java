package ra.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import ra.dto.request.ChangePassword;
import ra.dto.request.RegisterRequest;
import ra.dto.request.UserLogin;
import ra.dto.response.DisplayBook;
import ra.dto.response.JwtResponse;
import ra.dto.response.MessageResponse;
import ra.dto.response.UserDto;
import ra.jwt.JwtTokenProvider;
import ra.model.entity.*;
import ra.model.service.CartService;
import ra.model.service.BookService;
import ra.model.service.RoleService;
import ra.model.service.UserService;
import ra.security.CustomUserDetails;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("http://localhost:8080")
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

    private AuthenticationManager authenticationManager;
    private JwtTokenProvider tokenProvider;
    private UserService userService;
    private PasswordEncoder encoder;
    private RoleService roleService;
    private BookService bookService;
    private CartService cartService;
    private OAuth2UserService oAuth2UserService;

    @GetMapping("/getAllByFilter")
    public ResponseEntity<?> getAllByFilter(@RequestBody List<Filter> list) {
        List<Users> usersList = userService.getAllByFilter(list);
        List<UserDto> userDtos = new ArrayList<>();
        for (Users u : usersList) {
            UserDto userDto = userService.mapUserToUserDto(u);
            userDtos.add(userDto);
        }
        return ResponseEntity.ok().body(userDtos);
    }

    @GetMapping("/get_paging_and_Sort")
    public ResponseEntity<Map<String, Object>> getPagingAndSortByName(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam String direction,
            @RequestParam String sortBy) {
        Map<String, Object> data = new HashMap<>();

        try {
            Sort.Order order;
            if (direction.equals("asc")) {
                order = new Sort.Order(Sort.Direction.ASC, sortBy);
            } else {
                order = new Sort.Order(Sort.Direction.DESC, sortBy);
            }
            Pageable pageable = PageRequest.of(page, size, Sort.by(order));
            Page<Users> users = userService.getAllList(pageable);
            Page<UserDto> userDtos = users.map(users1 -> {
                UserDto userDto = userService.mapUserToUserDto(users1);
                return userDto;
            });
            data.put("usersDto", userDtos.getContent());
            data.put("total", userDtos.getSize());
            data.put("totalItems", userDtos.getTotalElements());
            data.put("totalPages", userDtos.getTotalPages());
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("/signIn")
    public ResponseEntity<ResponseObject> loginUser(@RequestBody UserLogin userLogin) {
        Users users = userService.findUsersByUserName(userLogin.getUserName());
        if (users.isStatusUser()) {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userLogin.getUserName(), userLogin.getPasswords())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            CustomUserDetails customUserDetail = (CustomUserDetails) authentication.getPrincipal();
            //Sinh JWT tra ve client
            String jwt = tokenProvider.generateToken(customUserDetail);
            //Lay cac quyen cua user
            List<String> listRoles = customUserDetail.getAuthorities().stream()
                    .map(item -> item.getAuthority()).collect(Collectors.toList());
            JwtResponse response = new JwtResponse(customUserDetail.getUserId(), customUserDetail.getFirstName(), customUserDetail.getLastName(), jwt, customUserDetail.getUsername(), customUserDetail.getEmail(),
                    customUserDetail.getAddress(), customUserDetail.getState(), customUserDetail.getCity(), customUserDetail.getPost(), customUserDetail.getPhone(), customUserDetail.getAvatar(), customUserDetail.getRanks(), listRoles, customUserDetail.getCarts().get(customUserDetail.getCarts().size() - 1));
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ok", "Welcome " + userLogin.getUserName(), response));
        } else {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(new ResponseObject("false", "Tài khoản " + userLogin.getUserName() + " của bạn đã bị khóa ! Vui lòng liên hệ với admin ", ""));
        }
    }

    @GetMapping("/search_by_userName")
    public ResponseEntity<Map<String, Object>> searchByUserName(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam String userName) {
        Map<String, Object> data = new HashMap<>();
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Users> users = userService.findByName(userName, pageable);
            Page<UserDto> userDtos = users.map(users1 -> {
                UserDto userDto = userService.mapUserToUserDto(users1);
                return userDto;
            });
            data.put("usersDto", userDtos.getContent());
            data.put("total", userDtos.getSize());
            data.put("totalItems", userDtos.getTotalElements());
            data.put("totalPages", userDtos.getTotalPages());
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/block_user/{userId}")
    public ResponseEntity<?> blockUser(@PathVariable int userId) {
        try {
            CustomUserDetails customUserDetail = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Users users = (Users) userService.findById(userId);
            if (customUserDetail.getAuthorities().size() > users.getListRoles().size()) {
                users.setStatusUser(false);
                userService.saveOrUpdate(users);
            }
            return ResponseEntity.ok("BlockUser successfully");
        } catch (Exception e) {
            return new ResponseEntity<>("BlockUser Error", HttpStatus.BAD_REQUEST);
        }
    }

    // dang nhap voi gg

    @GetMapping("/login_with_google")
    public RedirectView loginWithGoogle() {
        return new RedirectView("/login/oauth2/code/google");
    }

    @GetMapping("/success/profile")
    public ResponseEntity<Map<String, Object>> getUserInfo(@AuthenticationPrincipal OAuth2User principal) {
        Map<String, Object> userAttributes = principal.getAttributes();
        String name = (String) userAttributes.get("name");
        String email = (String) userAttributes.get("email");
        userAttributes.put("name", name);
        userAttributes.put("email", email);
        return new ResponseEntity<>(userAttributes, HttpStatus.OK);
    }

    //    ------------------    ĐĂNG KÝ   ----------------------
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest signupRequest) throws Throwable {
        if (userService.existsByUserName(signupRequest.getUserName())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Usermame is already"));
        }
        if (userService.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already"));
        }
        Users user = new Users();
        user.setUserName(signupRequest.getUserName());
        user.setPasswords(encoder.encode(signupRequest.getPasswords()));
        user.setAvatar(signupRequest.getAvatar());
        user.setLastName(signupRequest.getLastName());
        user.setFirstName(signupRequest.getFirstName());
        user.setEmail(signupRequest.getEmail());
        user.setPhone(signupRequest.getPhone());
        user.setAddress(signupRequest.getAddress());
        user.setState(signupRequest.getState());
        user.setCity(signupRequest.getCity());
        user.setPost(signupRequest.getPost());
        user.setBirtDate(signupRequest.getBirtDate());
        user.setRanks(0);
        user.setStatusUser(true);
        Set<String> strRoles = signupRequest.getListRoles();
        Set<Roles> listRoles = new HashSet<>();
        if (strRoles == null) {
            //User quyen mac dinh
            Roles userRole = (Roles) roleService.findByRoleName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found"));
            listRoles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Roles adminRole = null;
                        try {
                            adminRole = (Roles) roleService.findByRoleName(ERole.ROLE_ADMIN)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found 1"));
                        } catch (Throwable e) {
                            throw new RuntimeException(e);
                        }
                        listRoles.add(adminRole);
                    case "moderator":
                        Roles modRole = null;
                        try {
                            modRole = (Roles) roleService.findByRoleName(ERole.ROLE_MODERATOR)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found 2"));
                        } catch (Throwable e) {
                            throw new RuntimeException(e);
                        }
                        listRoles.add(modRole);
                    case "user":
                        Roles userRole = null;
                        try {
                            userRole = (Roles) roleService.findByRoleName(ERole.ROLE_USER)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found 3"));
                        } catch (Throwable e) {
                            throw new RuntimeException(e);
                        }
                        listRoles.add(userRole);
                }
            });
        }
        user.setListRoles(listRoles);
        Users result = userService.saveOrUpdate(user);
        Carts carts = new Carts();
        carts.setUsers(result);
        carts.setCartStatus(0);
        cartService.saveOrUpdate(carts);
        return ResponseEntity.ok(new MessageResponse("User registered successful"));
    }

    @GetMapping("/logOut")
    public ResponseEntity<?> logOut() {
        // Clear the authentication from server-side (in this case, Spring Security)
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logout successful");
    }


    //    -------------------   ROLE: ADMIN   -------------------------
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Users> getAllUser() {
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public Users getUserById(@PathVariable("userId") int userId) {
        return userService.findById(userId);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody RegisterRequest signupRequest) {
        try {
            return registerUser(signupRequest);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable("userId") int userId, @RequestBody RegisterRequest registerRequest) throws Throwable {
        Users userUpdate = (Users) userService.findById(userId);
        userUpdate.setStatusUser(registerRequest.isStatusUser());
        userUpdate.setRanks(registerRequest.getRanks());
        Set<String> strRoles = registerRequest.getListRoles();
        Set<Roles> listRoles = new HashSet<>();
        if (strRoles == null) {
            //User quyen mac dinh
            Roles userRole = (Roles) roleService.findByRoleName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found"));
            listRoles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Roles adminRole = null;
                        try {
                            adminRole = (Roles) roleService.findByRoleName(ERole.ROLE_ADMIN)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                        } catch (Throwable e) {
                            throw new RuntimeException(e);
                        }
                        listRoles.add(adminRole);
                    case "moderator":
                        Roles modRole = null;
                        try {
                            modRole = (Roles) roleService.findByRoleName(ERole.ROLE_MODERATOR)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                        } catch (Throwable e) {
                            throw new RuntimeException(e);
                        }
                        listRoles.add(modRole);
                    case "user":
                        Roles userRole = null;
                        try {
                            userRole = (Roles) roleService.findByRoleName(ERole.ROLE_USER)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                        } catch (Throwable e) {
                            throw new RuntimeException(e);
                        }
                        listRoles.add(userRole);
                }
            });
        }
        userUpdate.setListRoles(listRoles);
        userService.saveOrUpdate(userUpdate);
        return ResponseEntity.ok(new MessageResponse("Update successfully!"));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> deleteUser(@PathVariable("userId") int userId) {
        try {
            Users userDelete = (Users) userService.findById(userId);
            userDelete.setStatusUser(false);
            userService.saveOrUpdate(userDelete);
            return ResponseEntity.ok().body("Delete success");
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Delete fail");
        }
    }


    //    --------------------- ROLE : MODERATOR ----------------------------
    @GetMapping("getAllUserForModerator")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public List<Users> getAllUserForModerator() {
        List<Users> usersForModerator = new ArrayList<>();
        List<Users> listUser = userService.findAll();
        Set<Roles> roleUser = new HashSet<>();
        Roles userRole = new Roles(3, ERole.ROLE_USER);
        roleUser.add(userRole);
        for (Users user : listUser) {
            if (user.getListRoles().containsAll(roleUser) && user.getListRoles().size() == 1) {
                usersForModerator.add(user);
            }

        }
        return usersForModerator;
    }

    @PostMapping("createNewUser")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> createUserforModerator(@RequestBody RegisterRequest signupRequest) {
        if (userService.existsByUserName(signupRequest.getUserName())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Usermame is already"));
        }
        if (userService.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already"));
        }
        Users user = new Users();
        user.setUserName(signupRequest.getUserName());
        user.setPasswords(encoder.encode(signupRequest.getPasswords()));
        user.setAvatar(signupRequest.getAvatar());
        user.setLastName(signupRequest.getLastName());
        user.setFirstName(signupRequest.getFirstName());
        user.setEmail(signupRequest.getEmail());
        user.setPhone(signupRequest.getPhone());
        user.setAddress(signupRequest.getAddress());
        user.setState(signupRequest.getState());
        user.setCity(signupRequest.getCity());
        user.setPost(signupRequest.getPost());
        user.setBirtDate(signupRequest.getBirtDate());
        user.setRanks(0);
        user.setStatusUser(true);
        Set<Roles> roleUser = new HashSet<>();
        Roles userRole = new Roles(3, ERole.ROLE_USER);
        roleUser.add(userRole);
        user.setListRoles(roleUser);
        userService.saveOrUpdate(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully"));
    }

    @PutMapping("updateUserForModerator/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> updateUserForModerator(@PathVariable("userId") int userId, @RequestBody RegisterRequest registerRequest) {
        Users userUpdateModerator = (Users) userService.findById(userId);
        userUpdateModerator.setStatusUser(registerRequest.isStatusUser());
        userUpdateModerator.setRanks(registerRequest.getRanks());
        userService.saveOrUpdate(userUpdateModerator);
        return ResponseEntity.ok(new MessageResponse("Update successfully!"));
    }

    //    ----------------------- ROLE : USER ------------------------
    @PutMapping("updateUserForUser/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateUserForUser(@PathVariable("userId") int userId, @RequestBody RegisterRequest registerRequest) {
        if (userService.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already"));
        }
        Users userUpdateUser = (Users) userService.findById(userId);
        userUpdateUser.setFirstName(registerRequest.getFirstName());
        userUpdateUser.setLastName(registerRequest.getLastName());
        userUpdateUser.setPhone(registerRequest.getPhone());
        userUpdateUser.setAddress(registerRequest.getAddress());
        userUpdateUser.setEmail(registerRequest.getEmail());
        userUpdateUser.setState(registerRequest.getState());
        userUpdateUser.setCity(registerRequest.getCity());
        userUpdateUser.setPost(registerRequest.getPost());
        userUpdateUser.setAvatar(registerRequest.getAvatar());
        userUpdateUser.setBirtDate(registerRequest.getBirtDate());
        userService.saveOrUpdate(userUpdateUser);
        return ResponseEntity.ok(new MessageResponse("Update successfully!"));
    }

    @PostMapping("changePassword")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('USER')")
    public ResponseEntity<?> changePassword(@RequestBody ChangePassword changePassword) {
//        USER dang dang nhap
        CustomUserDetails usersChangePass = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Users users = userService.findUsersByUserName(usersChangePass.getUsername());
//        thong tin request
        String userName = changePassword.getUserName();
        String oldPass = changePassword.getOldPass();
        String newPass = changePassword.getNewPass();

        if (usersChangePass.getUsername().equals(userName) && BCrypt.checkpw(oldPass, usersChangePass.getPassword())) {
            users.setPasswords(encoder.encode(newPass));
            userService.saveOrUpdate(users);
        }
        return ResponseEntity.ok(new MessageResponse("Change password successfully!"));
    }

    @PutMapping("addWishList/{bookId}")
    public ResponseEntity<?> addToWishList(@PathVariable("bookId") int bookId) {
        Book book = bookService.getById(bookId);
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Users user = userService.findById(customUserDetails.getUserId());
        user.getWishList().add(book);
        try {
            userService.saveOrUpdate(user);
            return ResponseEntity.ok("Đã thêm sản phẩm vào danh mục ưa thích");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok("Có lỗi trong quá trình xử lý vui lòng thử lại!");
        }
    }

    @PutMapping("removeWishList/{bookId}")
    public ResponseEntity<?> removeWishList(@PathVariable("bookId") int bookId) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Users user = userService.findById(customUserDetails.getUserId());
        for (Book book : user.getWishList()) {
            if (book.getBookId() == bookId) {
                user.getWishList().remove(bookService.getById(bookId));
                break;
            }
        }
        try {
            userService.saveOrUpdate(user);
            return ResponseEntity.ok("Đã bỏ yêu thích sản phẩm này!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok("Có lỗi trong quá trình xử lý vui lòng thử lại!");
        }
    }

    @GetMapping("/new")
    List<Users> getAll() {
        return userService.getAll();
    }

    @GetMapping("getAllWishList")
    public ResponseEntity<?> getAllWishList() {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Book> listBook = bookService.getAllWishList(customUserDetails.getUserId());
        List<DisplayBook> list = new ArrayList<>();
        for (Book pro : listBook) {
            DisplayBook displayBook = new DisplayBook();
            displayBook.setBookId(pro.getBookId());
            displayBook.setBookName(pro.getBookName());
            displayBook.setBookTitle(pro.getBookTitle());
            displayBook.setDescriptions(pro.getDescriptions());
            displayBook.setSale(pro.getSale());
            displayBook.setExportPrice(pro.getExportPrice());
            displayBook.setImportPrice(pro.getImportPrice());
            displayBook.setCategory(pro.getCatalog());
            list.add(displayBook);
        }
        return ResponseEntity.ok(list);
    }
}
