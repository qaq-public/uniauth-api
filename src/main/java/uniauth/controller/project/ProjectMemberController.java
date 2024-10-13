package uniauth.controller.project;

import com.qaq.base.annotation.CheckPermission;
import com.qaq.base.model.Auth;
import com.qaq.base.response.ApiResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import uniauth.enums.PermissionConstant;
import uniauth.exception.ErrorMessageException;
import uniauth.jpa.entity.ProjectMember;
import uniauth.jpa.entity.User;
import uniauth.jpa.repository.ProjectMemberRepository;
import uniauth.jpa.repository.ProjectRepository;
import uniauth.jpa.repository.ProjectRoleRepository;
import uniauth.jpa.repository.UserRepository;
import uniauth.model.dto.UpdateProjectMemberDto;
import uniauth.model.dto.CreateProjectMemberDto;
import uniauth.service.NotifyService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/projects")
@RestController
public class ProjectMemberController {

    private final ProjectRoleRepository projectRoleRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final NotifyService notifyService;

    @GetMapping(value = "/{projectId}/members")
    public ApiResponse<List<ProjectMember>> list(@PathVariable String projectId) {
        var project = projectRepository.findByCode(projectId).orElseThrow();
        var projectMembers = projectMemberRepository.findByProjectId(project.getId());
        return new ApiResponse<>(projectMembers);
    }

    @CheckPermission(PermissionConstant.PROJECT_MEMBER_ADD)
    @Transactional
    @PostMapping(value = "/{projectId}/members")
    public ApiResponse<List<User>> create(@PathVariable String projectId, @RequestBody CreateProjectMemberDto params) {
        var project = projectRepository.findByCode(projectId).orElseThrow();
        var successUser = new ArrayList<User>();
        params.userIds().forEach(userId -> {
            var user = userRepository.findByOpenId(userId).orElse(null);
            if (user == null) {
                var larkUser = notifyService.retrieveUser(userId, params.userIdType());
                if (larkUser != null) {
                    user = User.builder()
                            .name(larkUser.getName())
                            .openId(larkUser.getOpenId())
                            .userId(larkUser.getUserId())
                            .email(larkUser.getEmail())
                            .nickname(larkUser.getNickname())
                            .avatar(larkUser.getAvatar().getAvatar72())
                            .leaver(false)
                            .projects(new HashSet<>())
                            .apps(new HashSet<>())
                            .build();
                    userRepository.save(user);
                } else {
                    throw new ErrorMessageException("用户不存在");
                }
            }
            var projectMember = projectMemberRepository
                    .findByProjectIdAndUserId(project.getId(), user.getId())
                    .orElse(ProjectMember.builder()
                            .project(project)
                            .user(user)
                            .build());
            projectMember.setRoles(new HashSet<>(projectRoleRepository.findAllById(params.roleIds())));
            if (params.remark() != null && !params.remark().isBlank()) {
                projectMember.setRemark(params.remark());
            }
            projectMemberRepository.save(projectMember);
            successUser.add(user);
        });
        return new ApiResponse<>(successUser);
    }

    @CheckPermission(PermissionConstant.PROJECT_MEMBER_MODIFY)
    @PutMapping(value = "/members/{id}")
    public ApiResponse<ProjectMember> update(@PathVariable Integer id,
                                          @RequestBody UpdateProjectMemberDto params) {
        var projectMember = projectMemberRepository.findById(id).orElseThrow();
        projectMember.setRoles(new HashSet<>(projectRoleRepository.findAllById(params.roleIds())));
        projectMember.setRemark(params.remark());
        projectMemberRepository.save(projectMember);
        return new ApiResponse<>(projectMember);

    }

    @CheckPermission(PermissionConstant.PROJECT_MEMBER_DELETE)
    @Transactional
    @DeleteMapping(value = "/members/{id}")
    public ApiResponse<Object> destroy(@PathVariable Integer id, @RequestAttribute Auth auth) {
        var projectMember = projectMemberRepository.findById(id).orElseThrow();
        if (!auth.getToken().getOpenid().equals(projectMember.getUser().getOpenId()) && !auth.havePermission(PermissionConstant.PROJECT_MEMBER_DELETE)) {
            return new ApiResponse<>(-1, "权限不足", null);
        }
        projectMemberRepository.deleteById(id);
        return new ApiResponse<>();
    }
}
