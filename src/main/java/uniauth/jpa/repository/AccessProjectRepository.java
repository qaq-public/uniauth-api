package uniauth.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uniauth.jpa.entity.AccessProject;

import java.util.Optional;

public interface AccessProjectRepository extends JpaRepository<AccessProject, Integer> {
    boolean existsByCode(String code);
}
