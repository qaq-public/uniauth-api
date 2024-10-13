package uniauth.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uniauth.jpa.entity.AppRole;

import java.util.List;
import java.util.Optional;

public interface AppRoleRepository extends JpaRepository<AppRole, Integer> {
    Optional<AppRole> findByNameAndAppId(String name, Integer appId);
    List<AppRole> findByAppId(Integer appId);
    List<AppRole> findByIdIn(List<Integer> roleId);
}

