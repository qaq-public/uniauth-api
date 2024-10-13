package uniauth.controller.project;

import com.qaq.base.annotation.CheckPermission;
import com.qaq.base.model.Auth;
import com.qaq.base.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import uniauth.enums.PermissionConstant;
import uniauth.event.ApproveAccessProjectEvent;
import uniauth.jpa.entity.AccessProject;
import uniauth.jpa.repository.AccessProjectRepository;
import uniauth.jpa.repository.UserRepository;
import uniauth.service.ProjectService;

import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/projects")
@RestController
public class ProjectApplyController {

    private final ProjectService projectService;
    private final AccessProjectRepository accessProjectRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final UserRepository userRepository;

    @GetMapping(value = "/applies")
    public ApiResponse<List<AccessProject>> list() {
        var sort = Sort.by(Sort.Order.desc("createTime"));
        return new ApiResponse<>(accessProjectRepository.findAll(sort));
    }

    @CheckPermission(PermissionConstant.PLATFORM_ADMIN)
    @PatchMapping("/applies/{id}")
    public ApiResponse<AccessProject> update(@PathVariable Integer id, @RequestAttribute Auth auth) {

        var accessProject = accessProjectRepository.findById(id).orElseThrow();

        if (accessProject.getApproveUser() != null && accessProject.getApproveTime() != null) {
            return new ApiResponse<>(-1, null, "项目已通过");
        }
        projectService.createProject(accessProject);
        accessProject.setApproveUser(userRepository.findByOpenId(auth.getToken().getEmail()).orElseThrow());
        accessProject.setApproveTime(new Date());
        accessProject.setStatus(1);
        accessProjectRepository.save(accessProject);
        applicationEventPublisher.publishEvent(new ApproveAccessProjectEvent(accessProject.getId()));
        return new ApiResponse<>(accessProject);
    }
}
