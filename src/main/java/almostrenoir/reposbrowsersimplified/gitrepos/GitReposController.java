package almostrenoir.reposbrowsersimplified.gitrepos;

import almostrenoir.reposbrowsersimplified.gitrepos.models.GitRepoDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("git-repos")
@CrossOrigin
@Validated
@RequiredArgsConstructor
public class GitReposController {

    private final GitReposService gitReposService;

    @GetMapping("by-user/{username}/no-forks")
    public Flux<GitRepoDTO> getUsersReposWithoutForks(
            @PathVariable @NotBlank(message = "Username cannot be blank") String username
    ) {
        return gitReposService.getUsersReposWithoutForks(username);
    }

}
