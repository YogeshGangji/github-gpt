package devpilot.backend.services;

import devpilot.backend.entity.User;
import devpilot.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TextEncryptor textEncryptor;

    @Transactional(readOnly = true)
    public User requiredById(UUID id)
    {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public String decryptAccessToken(User user)
    {
        return textEncryptor.decrypt(user.getAccessToken());
    }

    private static Long toLong(Object obj)
    {
        if (obj == null)
        {
            return null;
        }
        if (obj instanceof Number number)
        {
            return number.longValue();
        }
        String str = String.valueOf(obj).trim();
        if (str.isEmpty() || "null".equalsIgnoreCase(str))
        {
            return null;
        }
        return Long.parseLong(str);
    }

    @Transactional
    public User upsertFromGitHub(Map<String, Object> attributes, String accessToken, String scopes) {
        Object rawId = attributes.get("id") != null ? attributes.get("id") : attributes.get("githubId");
        if (rawId == null) {
            throw new IllegalArgumentException("GitHub user ID not found in OAuth2 attributes");
        }
        Long githubId = toLong(rawId);

        Object loginObj = attributes.get("login");
        String login = loginObj != null ? loginObj.toString() : "";

        Object nameObj = attributes.get("name");
        String name = (nameObj != null && !nameObj.toString().trim().isEmpty() && !"null".equalsIgnoreCase(nameObj.toString()))
                ? nameObj.toString().trim()
                : login;

        Object avatarObj = attributes.get("avatar_url");
        String avatarUrl = (avatarObj != null && !"null".equalsIgnoreCase(avatarObj.toString()))
                ? avatarObj.toString().trim()
                : null;

        String token = textEncryptor.encrypt(accessToken);

        User user = userRepository.findByGithubId(githubId).orElseGet(User::new);
        user.setGithubId(githubId);
        user.setGithubUsername(login);
        user.setDisplayName(name);
        user.setAvatarUrl(avatarUrl);
        user.setAccessToken(token);
        user.setTokenScopes(scopes);
        return userRepository.save(user);
    }
}

