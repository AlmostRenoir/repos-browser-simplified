package almostrenoir.reposbrowsersimplified.gitrepos;

import almostrenoir.reposbrowsersimplified.gitrepos.models.GitRepoDTO;
import almostrenoir.reposbrowsersimplified.gitrepos.models.GithubBranch;
import almostrenoir.reposbrowsersimplified.gitrepos.models.GithubRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class GitReposService {

    public static final String USERS_REPOS_ROUTE =  "/users/%s/repos";
    public static final String API_VERSION_HEADER_NAME = "X-GitHub-Api-Version";
    public static final String API_VERSION_HEADER_VALUE = "2022-11-28";
    public static final String GITHUB_JSON_CONTENT_TYPE = "application/vnd.github+json";

    private final String githubUrl;
    private final WebClient webClient;

    public GitReposService(@Value("${github.url}") String githubUrl, WebClient.Builder webClientBuilder) {
        this.githubUrl = githubUrl;
        this.webClient = webClientBuilder
                .defaultHeader(API_VERSION_HEADER_NAME, API_VERSION_HEADER_VALUE)
                .defaultHeader("Accept", GITHUB_JSON_CONTENT_TYPE)
                .build();
    }

    public Flux<GitRepoDTO> getUsersReposWithoutForks(String username) {
        return webClient.get()
                .uri(getUsersReposUrl(username))
                .retrieve()
                .bodyToFlux(GithubRepo.class)
                .filter(githubRepo -> !githubRepo.fork())
                .flatMap(this::collectBranches);
    }

    private String getUsersReposUrl(String username) {
        String usersReposUrl = githubUrl + USERS_REPOS_ROUTE;
        return String.format(usersReposUrl, username);
    }

    private Mono<GitRepoDTO> collectBranches(GithubRepo githubRepo) {
        return webClient.get()
                .uri(githubRepo.getCorrectBranchesUrl())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<GithubBranch>>() {})
                .map(githubBranches -> GitRepoDTO.fromGithubData(githubRepo, githubBranches));
    }
}
