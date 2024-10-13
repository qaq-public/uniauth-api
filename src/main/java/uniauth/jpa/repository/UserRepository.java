package uniauth.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uniauth.jpa.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findByGitEmail(String email);
    Optional<User> findByOpenId(String openId);
    List<User> findByOpenIdIn(List<String> openIds);

    @Query(value =  " select user.* from user " +
            " inner join user_role on user.user_id = user_role.user_id and user_role.app_id = :appId" +
            " where user_role.role_id = 2", nativeQuery = true)
    List<User> findAppAdmins(@Param("appId") Integer appId);

    @Query(value =  " select user.* from user " +
            " inner join user_role on user.user_id = user_role.user_id and user_role.project_id = :projectId" +
            " where user_role.role_id = 3", nativeQuery = true)
    List<User> findProjectAdmins(@Param("projectId") Integer projectId);
}
