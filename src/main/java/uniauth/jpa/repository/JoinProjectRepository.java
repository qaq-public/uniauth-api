package uniauth.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uniauth.jpa.entity.JoinProject;
import uniauth.jpa.entity.User;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface JoinProjectRepository extends JpaRepository<JoinProject, Integer> {
    List<JoinProject> findByProjectIdOrderByCreateTimeDesc(Integer projectId);
    boolean existsByProjectIdAndCreateUserAndApproveTime(Integer projectId, User createUser, Date date);
}
