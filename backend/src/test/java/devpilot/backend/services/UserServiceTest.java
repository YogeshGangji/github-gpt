package devpilot.backend.services;

import devpilot.backend.entity.User;
import devpilot.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TextEncryptor textEncryptor;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        when(textEncryptor.encrypt(anyString())).thenAnswer(invocation -> "encrypted_" + invocation.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void testUpsertFromGitHub_WithStandardGitHubAttributes() {
        // GitHub API returns numeric "id", "login", "name", "avatar_url"
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", 12345678);
        attributes.put("login", "octocat");
        attributes.put("name", "The Octocat");
        attributes.put("avatar_url", "https://avatars.githubusercontent.com/u/12345678");

        when(userRepository.findByGithubId(12345678L)).thenReturn(Optional.empty());

        User result = userService.upsertFromGitHub(attributes, "gho_testtoken123", "read:user,repo");

        assertNotNull(result);
        assertEquals(12345678L, result.getGithubId());
        assertEquals("octocat", result.getGithubUsername());
        assertEquals("The Octocat", result.getDisplayName());
        assertEquals("https://avatars.githubusercontent.com/u/12345678", result.getAvatarUrl());
        assertEquals("encrypted_gho_testtoken123", result.getAccessToken());
        assertEquals("read:user,repo", result.getTokenScopes());
    }

    @Test
    void testUpsertFromGitHub_WhenNameIsNull_FallbacksToLogin() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", 87654321L);
        attributes.put("login", "coder_dev");
        attributes.put("name", null); // User did not set a public display name on GitHub

        when(userRepository.findByGithubId(87654321L)).thenReturn(Optional.empty());

        User result = userService.upsertFromGitHub(attributes, "gho_sample", "read:user");

        assertNotNull(result);
        assertEquals(87654321L, result.getGithubId());
        assertEquals("coder_dev", result.getGithubUsername());
        // Must fallback to login, NOT the string "null"
        assertEquals("coder_dev", result.getDisplayName());
    }
}
