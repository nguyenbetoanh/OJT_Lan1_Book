package ra.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BookId")
    private int bookId;
    @Column(name = "BookName")
    private String bookName;
    @Column(name = "Descriptions")
    private String descriptions;
    @Column(name = "BookTitle")
    private String bookTitle;
    @Column(name = "ISBN")
    private String isbn;
    @Column(name = "EditionLanguage",columnDefinition = "nvarchar(50)")
    private String editionLanguage;
    @Column(name = "DatePublished")
    private Date datePublished;
    @Column(name = "Publisher")
    private String publisher;
    @Column(name = "ImportPrice")
    private float importPrice;
    @Column(name ="ExportPrice" )
    private float exportPrice;
    @Column(name ="Quantity" )
    private int quantity;
    @Column(name ="Sale" )
    private int sale;
    @Column(name ="BookStatus" )
    private boolean bookStatus;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CatalogId")
    private Category category;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "authorId", referencedColumnName = "authorId")
    private Author author;
    @OneToMany(mappedBy = "book")
    @JsonIgnore
    private List<LikeBook> listLikeBook = new ArrayList<>();
    @OneToMany (mappedBy = "book")
    @JsonIgnore
    private List<CartDetail> cartDetails= new ArrayList<>();
    @OneToMany(mappedBy = "book")
    @JsonIgnore
    private List<Comment> listComment = new ArrayList<>();
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable( name = "Tag_Book", joinColumns = @JoinColumn(name = "bookId"), inverseJoinColumns = @JoinColumn(name = "tagId"))
    private List<Tag> tagList= new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "UserId")
    private Users users;
    @OneToMany(mappedBy = "book")
    @JsonIgnore
    private List<Star> starList=new ArrayList<>();

}
