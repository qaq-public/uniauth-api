package uniauth.jpa.entity;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames={"name"}),
})
@Entity
public class ProjectRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String description;

    @JsonProperty("pre_defined")
    @Column(name = "pre_defined")
    private Boolean preDefined;

    @ManyToMany
    @JoinTable(
            name = "project_role_permission", // 中间表名
            joinColumns = @JoinColumn(name = "role_id"), // 关联到 ProjectRole 的外键
            inverseJoinColumns = @JoinColumn(name = "permission_id") // 关联到 Permission 的外键
    )
    private Set<Permission> permissions;

}