package ra.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "Category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CatalogId")
    private int catalogId;
    @Column(name = "CatalogName")
    private String catalogName;
    @Column(name = "CatalogStatus",columnDefinition = "nvarchar(100)")
    private boolean catalogStatus;
    @OneToMany(mappedBy = "category")
    @JsonIgnore
    List<Book> listBook = new ArrayList<>();
}
