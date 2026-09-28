package devpilot.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import devpilot.backend.entity.IndexStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

public record RepositoryResponse(
    UUID id,
    Long githubRepoId,

    String owner,

    String name,

    String fullName,

    @JsonProperty("isPrivate") Boolean isPrivate,

    String defaultBranch,

    String language,

    String htmlUrl,

    String description,

    IndexStatus indexStatus,

    Instant indexedAt,

    @JsonProperty("chunkCount") Integer chunksCount,

    Integer filesTotal,

    @JsonProperty("filesProcessed") Integer fileProcessed,

    String errorMessage) {

}
