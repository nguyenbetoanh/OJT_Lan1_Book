package ra.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ra.model.entity.Star;

@Repository
public interface StarRepository extends JpaRepository<Star,Integer> {
    Star findByBook_BookIdAndUsers_UserId(int bookId, int userId);
}
