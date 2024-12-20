package uniauth.jpa.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.Date;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class JoinProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @JsonProperty("create_user")
    @ManyToOne
    @JoinColumn(name = "create_user_id")
    private User createUser;

    @JsonProperty("create_time")
    private Date createTime;

    @JsonProperty("approve_user")
    @ManyToOne
    @JoinColumn(name = "approve_user_id")
    private User approveUser;

    @JsonProperty("approve_time")
    private Date approveTime;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    List<ProjectRole> roles;

    // 0: 申请中 1: 通过 2: 拒绝
    private Integer status;
}