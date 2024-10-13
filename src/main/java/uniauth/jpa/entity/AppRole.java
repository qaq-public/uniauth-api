package uniauth.jpa.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(
        uniqueConstraints = {@UniqueConstraint(columnNames={"name", "app_id"}),},
        indexes = {@Index(name = "idx_app_id", columnList = "app_id")}
)
@Entity
public class AppRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "app_id")
    private App app;

    private String description;

    @JsonProperty("pre_defined")
    @Column(name = "pre_defined")
    private Boolean preDefined;

    @ManyToMany
    @JoinTable(
            name = "app_role_permission", // 中间表名
            joinColumns = @JoinColumn(name = "role_id"), // 关联到 AppRole 的外键
            inverseJoinColumns = @JoinColumn(name = "permission_id") // 关联到 Permission 的外键
    )
    private Set<Permission> permissions;

}