package ra.model.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ra.model.entity.Category;
import ra.model.entity.Users;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Integer> {
    List <Category> findByCatalogNameContaining(String catalogName);
}
