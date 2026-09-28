package devpilot.backend.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Builder
@Entity
@Table(
        name = "repositories",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "github_repo_id"})

)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Repository {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id",  nullable = false)
    private UUID userId;

    @Column(name = "github_repo_id", nullable = false)
    private Long githubRepoId;

    @Column(nullable = false, length = 100)
    private String owner;

    @Column(nullable = false, length = 200)
    private  String name;

    @Column(name = "full_name",  nullable = false, length = 300)
    private String fullName;

    @Column(name = "is_private", nullable = false)
    private Boolean isPrivate;

    @Column(name = "default_branch", nullable = false, length = 100)
    private String defaultBranch;

    @Column(length = 100)
    private String language;

    @Column(name = "html_url" ,length = 500)
    private String htmlUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(length = 100, nullable = false,name = "index_status")
    @Builder.Default
    private IndexStatus indexStatus = IndexStatus.PENDING;

    private Instant indexedAt;

    @Builder.Default
    private Integer chunksCount = 0;

    @Builder.Default
    private Integer filesTotal = 0;

    @Builder.Default
    private Integer fileProcessed = 0;

    private String errorMessage;

    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updateDateAt;


    @PrePersist
    void onCreate() {
    Instant now = Instant.now();
    if(createdAt == null) {
        createdAt = now;
    }

    updateDateAt = now;
    if(indexStatus == null)
    {
        indexStatus = IndexStatus.PENDING;
    }
    }

}
