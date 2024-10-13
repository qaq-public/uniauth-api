package uniauth.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(indexes = {@Index(name = "idx_code", columnList = "code")})
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String code;
    private String name;
    private String description;
    private String stage;
    private String studio;
    private String avatar;

    @JsonProperty("game_type")
    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private Set<String> gameType;

    @JsonIgnore
    @OneToMany(mappedBy = "project")
    private Set<ProjectMember> members;

    public List<User> getProjectMembers() {
        return members.stream()
                .map(ProjectMember::getUser)
                .collect(Collectors.toList());
    }

    @JsonIgnore
    private Date createTime;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "create_user_id")
    private User createUser;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "last_modify_user_id")
    private User lastModifyUser;

    @JsonIgnore
    private Date lastModifyTime;
}