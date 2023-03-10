package ra.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ra.dto.request.CommentRequest;
import ra.model.entity.Author;
import ra.model.entity.Book;
import ra.model.entity.Comment;
import ra.model.entity.Users;
import ra.model.service.BookService;
import ra.model.service.CommentService;
import ra.model.service.UserService;
import ra.security.CustomUserDetails;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("http://localhost:8080")
@RequestMapping("/api/v1/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;
    @Autowired
    private BookService bookService;

    //    -------------------   ROLE: ADMIN & MODERATOR  -------------------------
    @GetMapping("/{commentId}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public Comment getCommentById(@PathVariable("commentId") int commentId) {
        return commentService.findById(commentId);
    }

    @GetMapping("getListCommentAndPaging")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Map<String, Object>> getPaging(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> pageListComment = commentService.getAllList(pageable);
        Map<String, Object> data = new HashMap<>();
//        Dữ liệu trả về trên 1 trang
        data.put("comments", pageListComment.getContent());
//        Tổng bản ghi trên 1 trang
        data.put("total", pageListComment.getSize());
//        Tổng dữ liệu
        data.put("totalComment", pageListComment.getTotalElements());
//        Tổng số trang
        data.put("totalPage", pageListComment.getTotalPages());
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @PutMapping("confirmComment/{commentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> ConfirmComment(@PathVariable("commentId") int commentId, @RequestBody CommentRequest commentRequest) {
        Comment commentConfirm = commentService.findById(commentId);
        List<Comment> listChild = commentService.findChildById(commentId);
        List<Comment> listParent = commentService.findParentById(commentId);

        commentConfirm.setCommentStatus(commentRequest.isCommentStatus());
        commentService.saveOrUpdate(commentConfirm);

        if (listParent != null && commentRequest.isCommentStatus()) {
            for (Comment cmPa : listParent) {
                cmPa.setCommentStatus(true);
                commentService.saveOrUpdate(cmPa);
            }
        }

        List<Comment> listChildUpdate = commentService.findByCommentIdIn(commentRequest.getListCommentId());
        if (listChild != null) {
            if (commentRequest.isCommentStatus()) {
                for (Comment cmChild : listChildUpdate) {
                    cmChild.setCommentStatus(true);
                    commentService.saveOrUpdate(cmChild);
                }
            } else {
                for (Comment cm : listChild) {
                    cm.setCommentStatus(false);
                    commentService.saveOrUpdate(cm);
                }
            }
        }

        List<Comment> listChauUpdate = commentService.findByCommentIdIn(commentRequest.getListCommentIdChild());
        if (listChauUpdate != null && commentRequest.isCommentStatus()) {
            for (Comment cmChau : listChauUpdate) {
                cmChau.setCommentStatus(true);
                commentService.saveOrUpdate(cmChau);
            }
        }

        return ResponseEntity.ok().body("Confirm comment success");
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public void deleteComment(@PathVariable("commentId") int commentId) {
        Comment commentDelete = commentService.findById(commentId);
        List<Comment> listChild = commentService.findChildById(commentId);
        commentDelete.setCommentStatus(false);
        if (listChild!=null) {
            for (Comment cm : listChild) {
                cm.setCommentStatus(false);
                commentService.saveOrUpdate(cm);
            }
        }
        commentService.saveOrUpdate(commentDelete);
    }

    @GetMapping("getCommentPagingAndSortById")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Map<String, Object>> getPagingAndSortingById(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam("direction") String direction) {
        Sort.Order order = null;
        if (direction.equals("asc")){
            order = new Sort.Order(Sort.Direction.ASC,"commentId");
        } else if (direction.equals("des")){
            order = new Sort.Order(Sort.Direction.DESC,"commentId");
        }
        Pageable pageable = PageRequest.of(page, size,Sort.by(order));
        Page<Comment> pageComment = commentService.getAllList(pageable);
        Map<String, Object> data = new HashMap<>();
//        Dữ liệu trả về trên 1 trang
        data.put("comments",pageComment.getContent());
//        Tổng bản ghi trên 1 trang
        data.put("total", pageComment.getSize());
//        Tổng dữ liệu
        data.put("totalComments", pageComment.getTotalElements());
//        Tổng số trang
        data.put("totalPage", pageComment.getTotalPages());
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("searchCommentByBookName")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Map<String, Object>> getListCommentSearchByBookNameAndPaging(
            @RequestParam("searchName") String searchName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {
        Map<String, Object> data = new HashMap<>();
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Comment> pageListComment = commentService.findByBook_BookName(searchName,pageable);
//        Dữ liệu trả về trên 1 trang
            data.put("comment",pageListComment.getContent());
//        Tổng bản ghi trên 1 trang
            data.put("total", pageListComment.getSize());
//        Tổng dữ liệu
            data.put("totalComments", pageListComment.getTotalElements());
//        Tổng số trang
            data.put("totalPage", pageListComment.getTotalPages());
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(data,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("searchCommentByUserName")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Map<String, Object>> getListCommentSearchByUserNameAndPaging(
            @RequestParam("searchName") String searchName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {
        Map<String, Object> data = new HashMap<>();
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Comment> pageListComment = commentService.findByUsers_UserName(searchName,pageable);
//        Dữ liệu trả về trên 1 trang
            data.put("comment",pageListComment.getContent());
//        Tổng bản ghi trên 1 trang
            data.put("total", pageListComment.getSize());
//        Tổng dữ liệu
            data.put("totalComments", pageListComment.getTotalElements());
//        Tổng số trang
            data.put("totalPage", pageListComment.getTotalPages());
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(data,HttpStatus.BAD_REQUEST);
        }
    }

    //    ----------------------- ROLE : USER ------------------------
    @PostMapping()
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('USER')")
    public ResponseEntity<?> createComment(@RequestBody CommentRequest comment) {
        CustomUserDetails usersChangePass = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Users users = userService.findUsersByUserName(usersChangePass.getUsername());
        Book book = bookService.getById(comment.getBookId());

        Comment commentNew = new Comment();
        commentNew.setContent(comment.getContent());
        commentNew.setUsers(users);
        commentNew.setBook(book);
        commentNew.setCommentParentId(comment.getCommentParentId());
        commentNew.setCommentStatus(false);
        commentService.saveOrUpdate(commentNew);

        return ResponseEntity.ok().body("Comment success");
    }

    @PutMapping("/{commentId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateComment(@PathVariable("commentId") int commentId, @RequestBody CommentRequest commentRequest) {
        Comment commentUpdate = commentService.findById(commentId);

        commentUpdate.setBook(commentUpdate.getBook());
        commentUpdate.setUsers(commentUpdate.getUsers());
        commentUpdate.setCommentParentId(commentUpdate.getCommentId());
        commentUpdate.setContent(commentRequest.getContent());
        commentUpdate.setCommentStatus(false);
        commentService.saveOrUpdate(commentUpdate);

        return ResponseEntity.ok().body("Update comment success");
    }


}