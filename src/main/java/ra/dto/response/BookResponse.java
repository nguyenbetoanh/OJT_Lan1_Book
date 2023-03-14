package ra.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import ra.model.entity.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Data
public class BookResponse {
    private int bookId;
    private String bookName;
    private String descriptions;
    private String bookTitle;
    private String isbn;
    private String editionLanguage;
    private Date datePublished;
    private String publisher;
    private float importPrice;
    private float exportPrice;
    private int quantity;
    private int sale;
    private boolean bookStatus;
    private Category catalog;
    private Author author;
    private int countLike;
    private List<Comment> listComment = new ArrayList<>();
    private List<Tag> tagList= new ArrayList<>();
    private float avgStar;
}
