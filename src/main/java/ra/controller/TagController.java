package ra.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ra.dto.request.TagRequest;
import ra.model.entity.Tag;
import ra.model.service.TagService;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin("http://localhost:8080")
@RequestMapping("/api/v1/tag")
@AllArgsConstructor
public class TagController {
    private TagService tagService;

    @GetMapping("/get_paging_and_sort")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Map<String, Object>> getPagingAndSort(
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
            Page<Tag> tags = tagService.getAllList(pageable);
            data.put("tags", tags.getContent());
            data.put("total", tags.getSize());
            data.put("totalItems", tags.getTotalElements());
            data.put("totalPages", tags.getTotalPages());
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/search_by_name")
    public ResponseEntity<Map<String, Object>> searchByName(
            @RequestParam  String tagName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> data = new HashMap<>();
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Tag> tags = tagService.findByName(tagName, pageable);
            data.put("tags", tags.getContent());
            data.put("total", tags.getSize());
            data.put("totalItems", tags.getTotalElements());
            data.put("totalPages", tags.getTotalPages());
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/creat_new_tag")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> creatNewTag(@RequestBody TagRequest request) {
        try {
            Tag result = tagService.saveOrUpdate(tagService.mapTagRequestToTag(request));
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{tagId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> update(@PathVariable int tagId, @RequestBody Tag tag) {
        try {
            Tag tagUpdate = tagService.findById(tagId);
            tagUpdate.setTagName(tag.getTagName());
            tagUpdate.setTagStatus(tag.isTagStatus());
            Tag result = tagService.saveOrUpdate(tagUpdate);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @PatchMapping("/delete/{tagId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> delete(@PathVariable int tagId){
        try {
            Tag tag= tagService.findById(tagId);
            tag.setTagStatus(false);
            Tag result=tagService.saveOrUpdate(tag);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
