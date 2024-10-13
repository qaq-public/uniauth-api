package uniauth.jpa.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.Date;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class AccessProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String code;
    private String name;
    private String studio;
    private String stage;
    private String avatar;
    private String description;

    @JsonProperty("game_type")
    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private Set<String> gameType;

    @JsonProperty("create_user")
    @ManyToOne
    @JoinColumn(name = "create_user_id", nullable = false)
    private User createUser;

    private Date createTime;

    @JsonProperty("approve_user")
    @ManyToOne
    @JoinColumn(name = "approve_user_id")
    private User approveUser;

    @JsonProperty("approve_time")
    private Date approveTime;

    // 0: 申请中 1: 通过 2: 拒绝
    private Integer status;
}