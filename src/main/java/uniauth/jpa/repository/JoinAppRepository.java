package uniauth.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uniauth.jpa.entity.JoinApp;
import uniauth.jpa.entity.User;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface JoinAppRepository extends JpaRepository<JoinApp, Integer> {
    List<JoinApp> findByAppIdOrderByCreateTimeDesc(Integer appId);
    boolean existsByAppIdAndCreateUserAndApproveTime(Integer appId, User createUser, Date date);
}
