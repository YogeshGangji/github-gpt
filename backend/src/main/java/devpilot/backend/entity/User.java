package devpilot.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false, name = "github_id")
    private Long githubId;

    @Column(name = "github_username", nullable = false, length = 100)
    private String githubUsername;

    @Column(nullable = false)
    private String displayName;

    private String avatarUrl;

    @Column(columnDefinition = "TEXT")
    private String accessToken;
    private String tokenScopes;

    @Column(updatable = false)
    private Instant  createdAt;

    @PrePersist
    void onCreate() {
        if(this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }
}
