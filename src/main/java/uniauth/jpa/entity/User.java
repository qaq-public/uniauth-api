package uniauth.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(indexes = {@Index(name = "idx_open_id", columnList = "open_id")})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

    @JsonProperty("userid")
    @Column(name = "user_id")
    private String userId;

    @JsonProperty("openid")
    @Column(name = "open_id")
    private String openId;
    private String nickname;
    private String email;
    private String avatar;

    @JsonProperty("default_project_id")
    @Column(name = "default_project_id", insertable = false, updatable = false)
    private Integer defaultProjectId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "default_project_id")
    private Project defaultProject;

    private Boolean leaver;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Set<ProjectMember> projects;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Set<AppMember> apps;

    @JsonIgnore
    public List<Project> getJoinedProjects() {
        return projects.stream()
                .map(ProjectMember::getProject)
                .toList();
    }

    @JsonIgnore
    public List<App> getJoinedApps() {
        return apps.stream()
                .map(AppMember::getApp)
                .toList();
    }

    @JsonIgnore
    private String accessToken;
    @JsonIgnore
    private String tokenType;
    @JsonIgnore
    private long expiresIn;
    @JsonIgnore
    private String refreshToken;
    @JsonIgnore
    private long refreshExpiresIn;
    @JsonIgnore
    private String jsapiTicket;
    @JsonIgnore
    private long jsapiTicketExpiresIn;
    @JsonIgnore
    private String gitEmail;

}