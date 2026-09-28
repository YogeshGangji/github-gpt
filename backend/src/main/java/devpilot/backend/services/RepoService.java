package devpilot.backend.services;

import devpilot.backend.dto.IndexStatusResponse;
import devpilot.backend.dto.RepositoryResponse;
import devpilot.backend.entity.Repository;
import devpilot.backend.entity.User;
import devpilot.backend.exceptions.NotFoundException;
import devpilot.backend.repository.RepositoryRepository;
import devpilot.backend.services.github.GithubApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RepoService {

    private final RepositoryRepository repositoryRepository;
    private final UserService userService;
    private final GithubApiClient githubApiClient;

    @Transactional
    public List<RepositoryResponse> syncAndListRepo(UUID userId)
    {
        User user = userService.requiredById(userId);

        String accessToken = userService.decryptAccessToken(user);

        List<Map<String, Object>> remoteRepos = githubApiClient.listUserRepos(accessToken);

        List<Repository> saved = new ArrayList<>();

        for(Map<String, Object> remote: remoteRepos)
        {
            Long gitHubRepoId = toLong(remote.get("id"));
            Repository repo = repositoryRepository.findByUserIdAndGithubRepoId(userId,gitHubRepoId)
                    .orElseGet(Repository::new);

            String fullName = String.valueOf(remote.get("full_name"));
            String[] parts = fullName.split("/", 2);

            repo.setUserId(userId);
            repo.setGithubRepoId(gitHubRepoId);
            repo.setOwner(parts.length > 0 ? parts[0] : String.valueOf(remote.get("owner")));
            repo.setName(parts.length > 1 ? parts[1] : String.valueOf(remote.get("name")));
            repo.setFullName(fullName);
            repo.setIsPrivate(Boolean.TRUE.equals(remote.get("private")));
            repo.setDefaultBranch(remote.get("default_branch") != null ? String.valueOf(remote.get("default_branch")) : "main");
            repo.setLanguage(remote.get("language") != null ? String.valueOf(remote.get("language")) : null);
            repo.setHtmlUrl(remote.get("html_url") != null ? String.valueOf(remote.get("html_url")) : null);
            repo.setDescription(remote.get("description") != null ? String.valueOf(remote.get("description")) : null);
            repo.setUpdateDateAt(Instant.now());
            if(repo.getOwner() == null || repo.getOwner().isBlank())
            {
                Object ownerObj = remote.get("owner");
                if(ownerObj instanceof Map<?,?>  ownweMap && ownweMap.get("login") != null)
                    repo.setOwner(ownweMap.get("login").toString());
            }

            saved.add(repositoryRepository.save(repo));

        }
            return saved.stream()
                    .sorted((a, b ) -> a.getFullName().compareToIgnoreCase(b.getFullName()))
                    .map(this::toResponse)
                    .toList();

    }

    @Transactional(readOnly = true)
    public List<RepositoryResponse> listSorted(UUID userId)
    {
        return repositoryRepository.findByUserIdOrderByFullNameAsc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Repository requiredOwned(UUID repositoryId, UUID userId)
    {
        return repositoryRepository.findByIdAndUserId(repositoryId, userId)
                .orElseThrow(()-> new NotFoundException("Repository Not Found"));
    }

    @Transactional(readOnly = true)
    public IndexStatusResponse status(UUID repoId, UUID userId)
    {
        Repository repo = requiredOwned(repoId, userId);
        return new IndexStatusResponse(
                repo.getId(),
                repo.getIndexStatus(),
                repo.getFilesTotal(),
                repo.getFileProcessed(),
                repo.getChunksCount(),
                repo.getIndexedAt(),
                repo.getErrorMessage()
        );
    }

    public RepositoryResponse toResponse(Repository repo)
    {
        return new RepositoryResponse(
            repo.getId(),
            repo.getGithubRepoId(),
            repo.getOwner(),
            repo.getName(),
            repo.getFullName(),
            repo.getIsPrivate(),
            repo.getDefaultBranch(),
            repo.getLanguage(),
            repo.getHtmlUrl(),
            repo.getDescription(),
            repo.getIndexStatus(),
            repo.getIndexedAt(),
            repo.getChunksCount(),
            repo.getFilesTotal(),
            repo.getFileProcessed(),
            repo.getErrorMessage());
    }

    private static Long toLong(Object o) {
        if(o instanceof Number number) {
            return number.longValue();
        }
            return Long.parseLong(String.valueOf(o));
    }
}
