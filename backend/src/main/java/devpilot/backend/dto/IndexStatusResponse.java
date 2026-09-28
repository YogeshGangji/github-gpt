package devpilot.backend.dto;

import devpilot.backend.entity.IndexStatus;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public record IndexStatusResponse(
                UUID repositoryId,
                IndexStatus indexStatus,

                @JsonProperty("filesTotal") int fileTotal,

                @JsonProperty("filesProcessed") int fileProcessed,

                int chunkCount,
                Instant indexedAt,
                String errorMessage) {

}
