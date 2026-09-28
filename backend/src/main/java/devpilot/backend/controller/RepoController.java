package devpilot.backend.controller;

import devpilot.backend.dto.IndexStatusResponse;
import devpilot.backend.dto.RepositoryResponse;
import devpilot.backend.entity.Repository;
import devpilot.backend.repository.RepositoryRepository;
import devpilot.backend.security.CurrentUser;
import devpilot.backend.services.RepoService;
import devpilot.backend.services.indexing.IndexingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/repos")
@RequiredArgsConstructor
public class RepoController {

    private final RepoService repoService;
    private final CurrentUser currentUser;
    private final IndexingService indexingService;

    @GetMapping
    public List<RepositoryResponse> list(@RequestParam(name = "refresh", defaultValue = "true") boolean refresh){
        UUID userId = currentUser.require().getId();
        if(refresh){
            return repoService.syncAndListRepo(userId);
        }
        return repoService.listSorted(userId);
    }

    @GetMapping("/{id}")
    public RepositoryResponse get(@PathVariable("id") UUID id){
        UUID userId = currentUser.require().getId();

        return repoService.toResponse(repoService.requiredOwned(id, userId));

    }

    @PostMapping("/{id}/index")
    public ResponseEntity<RepositoryResponse> index(@PathVariable("id") UUID id)
    {
        UUID userId= currentUser.require().getId();
        Repository repo = indexingService.startIndexing(id, userId);
        indexingService.indexAsync(id, userId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(repoService.toResponse(repo));
    }

    @GetMapping("/{id}/status")
    public IndexStatusResponse getStatus(@PathVariable("id") UUID id){
       UUID userId = currentUser.require().getId();
       return repoService.status(id, userId);
    }
}
