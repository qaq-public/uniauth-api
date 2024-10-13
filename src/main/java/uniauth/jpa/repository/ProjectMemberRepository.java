package uniauth.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uniauth.jpa.entity.ProjectMember;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Integer> {
    List<ProjectMember> findByProjectId(Integer projectId);
    Optional<ProjectMember> findByProjectIdAndUserId(Integer projectId, Integer userId);
    boolean existsByProjectIdAndUserId(Integer projectId, Integer userId);
}
