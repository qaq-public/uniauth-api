package uniauth.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uniauth.jpa.entity.Permission;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, Integer> {
    List<Permission> findByAppId(Integer appId);

    @Query(value = "SELECT DISTINCT p.* " +
            "FROM permission p " +
            "JOIN app_role_permission arp ON p.id = arp.permission_id " +
            "JOIN app_role ar ON ar.id = arp.role_id " +
            "JOIN app_member_role amr ON ar.id = amr.app_role_id " +
            "JOIN app_member am ON amr.app_member_id = am.id " +
            "WHERE am.user_id = :userId AND am.app_id = :appId",
            nativeQuery = true)
    List<Permission> findPermissionsByUserAndApp(@Param("userId") Integer userId, @Param("appId") Integer appId);

    @Query(value = "SELECT DISTINCT p.name " +
            "FROM permission p " +
            "JOIN app_role_permission arp ON p.id = arp.permission_id " +
            "JOIN app_role ar ON ar.id = arp.role_id " +
            "JOIN app_member_role amr ON ar.id = amr.app_role_id " +
            "JOIN app_member am ON amr.app_member_id = am.id " +
            "WHERE am.user_id = :userId AND am.app_id = :appId",
            nativeQuery = true)
    List<String> findPermissionNamesByUserAndApp(@Param("userId") Integer userId, @Param("appId") Integer appId);

    @Query(value = "select DISTINCT permission.name from permission " +
            "inner join user_extra_permission on user_extra_permission.permission_id = permission.id" +
            " where user_extra_permission.user_id =:userId" +
            " and user_extra_permission.app_id =:appId", nativeQuery = true)
    List<String> findExtraPermissionNamesByUserAndApp(@Param("userId") Integer userId, @Param("appId") Integer appId);

    @Query(value = "SELECT DISTINCT p.name FROM permission p " +
            "JOIN project_role_permission prp ON p.id = prp.permission_id " +
            "JOIN project_role pr ON pr.id = prp.role_id " +
            "JOIN project_member_role pmr ON pr.id = pmr.project_role_id " +
            "JOIN project_member pm ON pmr.project_member_id = pm.id " +
            "WHERE pm.user_id = :userId AND pm.project_id = :projectId AND p.app_id = :appId",
            nativeQuery = true)
    List<String> findPermissionNamesByUserAndProjectAndApp(@Param("userId") Integer userId,
                                             @Param("projectId") Integer projectId,
                                             @Param("appId") Integer appId);
}

