package uniauth.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uniauth.jpa.entity.App;

import java.util.List;
import java.util.Optional;

public interface AppRepository extends JpaRepository<App, Integer> {
    Optional<App> findByCode(String code);
}
