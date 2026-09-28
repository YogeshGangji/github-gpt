package devpilot.backend.dto;

import lombok.Data;

import java.util.UUID;

public record UserResponse (
    UUID id,
    Long githubId,
    String githubUsername,
    String displayName,
    String avatarUrl
){

}
