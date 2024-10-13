package uniauth.controller.project;

import com.qaq.base.annotation.CheckPermission;
import com.qaq.base.model.Auth;
import com.qaq.base.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;
import uniauth.enums.PermissionConstant;
import uniauth.event.ApproveJoinProjectEvent;
import uniauth.event.JoinProjectEvent;
import uniauth.event.RejectJoinProjectEvent;
import uniauth.jpa.entity.JoinProject;
import uniauth.jpa.entity.ProjectMember;
import uniauth.jpa.repository.*;
import uniauth.model.dto.ProjectJoinMemberDto;
import uniauth.service.ProjectService;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/projects")
@RestController
public class ProjectJoinController {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final JoinProjectRepository joinProjectRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ProjectRoleRepository projectRoleRepository;

    @PostMapping(value = "/{projectId}/joins")
    public ApiResponse<JoinProject> joinProject(@PathVariable String projectId, @RequestAttribute Auth auth) {
        var project = projectRepository.findByCode(projectId).orElseThrow();
        var user = userRepository.findByEmail(auth.getToken().getEmail()).orElseThrow();
        var isMember = projectMemberRepository.existsByProjectIdAndUserId(project.getId(), user.getId());
        if (isMember) {
            return new ApiResponse<>(-1, null, "你已在该项目组");
        }
        if (joinProjectRepository.existsByProjectIdAndCreateUserAndApproveTime(project.getId(), user, null)) {
            return new ApiResponse<>(-1, null, "你已申请加入该项目组, 请勿重复申请");
        }
        var joinProject = JoinProject
                .builder()
                .id(null)
                .project(project)
                .createUser(user)
                .createTime(new Date())
                .status(0)
                .build();
        joinProjectRepository.save(joinProject);
        applicationEventPublisher.publishEvent(new JoinProjectEvent(joinProject.getId()));
        return new ApiResponse<>(joinProject);
    }

    @GetMapping(value = "/{projectId}/joins")
    public ApiResponse<List<JoinProject>> listJoinProject(@PathVariable String projectId) {
        var project = projectRepository.findByCode(projectId).orElseThrow();
        var projectJoinApplies = joinProjectRepository.findByProjectIdOrderByCreateTimeDesc(project.getId());
        return new ApiResponse<>(projectJoinApplies);
    }

    @CheckPermission(PermissionConstant.PROJECT_MEMBER_ADD)
    @PatchMapping("/joins/{id}")
    public ApiResponse<JoinProject> addAppManager(@PathVariable Integer id,
                                                  @RequestBody ProjectJoinMemberDto params,
                                                  @RequestAttribute Auth auth) {
        var joinProject = joinProjectRepository.findById(id).orElseThrow();
        var approveUser = userRepository.findByOpenId(auth.getToken().getOpenid()).orElseThrow();
        var projectMember = projectMemberRepository
                .findByProjectIdAndUserId(joinProject.getProject().getId(), joinProject.getCreateUser().getId())
                .orElse(ProjectMember.builder()
                        .project(joinProject.getProject())
                        .user(joinProject.getCreateUser())
                        .build());
        var roles = projectRoleRepository.findByIdIn(params.roleIds());
        projectMember.setRoles(new HashSet<>(roles));

        // 添加备注
        if (params.remark() != null && !params.remark().isBlank()) {
            projectMember.setRemark(params.remark());

        }
        projectMemberRepository.save(projectMember);

        joinProject.setApproveUser(approveUser);
        joinProject.setApproveTime(new Date());
        joinProject.setRoles(roles);
        joinProject.setStatus(1);
        joinProjectRepository.save(joinProject);
        applicationEventPublisher.publishEvent(new ApproveJoinProjectEvent(joinProject.getId()));
        return new ApiResponse<>(joinProject);
    }

    @CheckPermission(PermissionConstant.PROJECT_MEMBER_ADD)
    @PatchMapping("/joins/{id}/reject")
    public ApiResponse<JoinProject> reject(@PathVariable Integer id, @RequestAttribute Auth auth) {
        var joinProject = joinProjectRepository.findById(id).orElseThrow();
        var approveUser = userRepository.findByOpenId(auth.getToken().getOpenid()).orElseThrow();
        joinProject.setApproveUser(approveUser);
        joinProject.setApproveTime(new Date());
        joinProject.setStatus(2);
        joinProjectRepository.save(joinProject);
        applicationEventPublisher.publishEvent(new RejectJoinProjectEvent(joinProject.getId()));
        return new ApiResponse<>(joinProject);
    }
}
