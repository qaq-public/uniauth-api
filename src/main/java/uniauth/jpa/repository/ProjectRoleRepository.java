package uniauth.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import uniauth.jpa.entity.ProjectRole;

public interface ProjectRoleRepository extends JpaRepository<ProjectRole, Integer> {
    List<ProjectRole> findByIdIn(List<Integer> ids);
    @Query(value = "select role.* from role " +
            " inner join user_role on role_id = role.id " +
            " where user_id =:userId" +
            " and project_id=:projectId ", nativeQuery = true)
    List<ProjectRole> findUserProjectRoles(@Param("userId") String userId, @Param("projectId") Integer projectId);

}

