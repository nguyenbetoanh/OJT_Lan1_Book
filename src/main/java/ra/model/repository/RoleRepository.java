package ra.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ra.model.entity.ERole;
import ra.model.entity.Roles;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Roles,Integer> {
    Optional<Roles> findByRoleName(ERole roleName);

}