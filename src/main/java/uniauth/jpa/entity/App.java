package uniauth.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(indexes = {@Index(name = "idx_code", columnList = "code")})
public class App {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true, nullable = false)
    private String code;
    @Column(unique = true, nullable = false)
    private String name;
    private String description;
    private String avatar;

    @JsonProperty("share_role")
    private Boolean shareRole = true;

    @JsonProperty("share_member")
    private Boolean shareMember;

    @OneToMany(mappedBy = "app", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AppMember> members;

    @JsonIgnore
    private Date createTime;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "create_user_id")
    private User createUser;
}