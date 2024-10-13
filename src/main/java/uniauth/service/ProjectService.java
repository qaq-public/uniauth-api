package uniauth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uniauth.enums.DbConstant;
import uniauth.jpa.entity.*;
import uniauth.jpa.repository.*;

import java.util.*;

@RequiredArgsConstructor
@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectRoleRepository projectRoleRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createProject(AccessProject accessProject) {
        var user = accessProject.getCreateUser();
        var project = Project.builder()
                .code(accessProject.getCode())
                .name(accessProject.getName())
                .studio(accessProject.getStudio())
                .stage(accessProject.getStudio())
                .avatar(accessProject.getAvatar())
                .gameType(accessProject.getGameType())
                .createUser(accessProject.getCreateUser())
                .createTime(new Date())
                .build();
        projectRepository.save(project);
        user.getProjects().add(ProjectMember
                .builder()
                .project(project)
                .user(user)
                .roles(new HashSet<>(List.of(projectRoleRepository.findById(DbConstant.ADMIN).orElseThrow())))
                .build());
        userRepository.save(user);
    }
}
