package uniauth.controller.project;

import com.qaq.base.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import uniauth.jpa.entity.ProjectRole;
import uniauth.jpa.repository.ProjectRoleRepository;
import uniauth.model.dto.PartialUpdateProjectRoleDto;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping(value = "/projects")
@RestController
public class ProjectRoleController {

    private final ProjectRoleRepository projectRoleRepository;

    @GetMapping("/roles")
    public ApiResponse<List<ProjectRole>> list() {
        var roles = projectRoleRepository.findAll(Sort.by("id"));
        return new ApiResponse<>(roles);
    }

    @PostMapping(value = "/roles")
    public ApiResponse<ProjectRole> create(@RequestBody ProjectRole projectRole) {
        var role = projectRoleRepository.save(ProjectRole.builder()
                .name(projectRole.getName())
                .description(projectRole.getDescription())
                .preDefined(true)
                .build());
        return new ApiResponse<>(role);
    }

    @PatchMapping(value = "/roles/{roleId}")
    public ApiResponse<ProjectRole> partialUpdate(@PathVariable Integer roleId, @RequestBody PartialUpdateProjectRoleDto params) {
        var role = projectRoleRepository.findById(roleId).orElseThrow();
        role.setName(params.roleName());
        role.setDescription(params.description());
        projectRoleRepository.save(role);
        return new ApiResponse<>(role);
    }

    @DeleteMapping(value = "/roles/{roleId}")
    public ApiResponse<?> destroy(@PathVariable Integer roleId) {
        projectRoleRepository.deleteById(roleId);
        return new ApiResponse<>();
    }

}
