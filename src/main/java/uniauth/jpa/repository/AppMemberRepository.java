package uniauth.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uniauth.jpa.entity.AppMember;

import java.util.List;
import java.util.Optional;

public interface AppMemberRepository extends JpaRepository<AppMember, Integer> {
    List<AppMember> findByAppId(Integer appId);
    Optional<AppMember> findByUserIdAndAppId(Integer userId, Integer appId);
}
