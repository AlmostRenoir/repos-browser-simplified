package almostrenoir.reposbrowsersimplified.gitrepos;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GitReposControllerTest {

    private static final String GET_USERS_REPOS_WITHOUT_FORKS_URI = "/git-repos/by-user/%s/no-forks";

    @Value("${github.port}")
    private int githubPort;

    @Autowired
    private WebTestClient webTestClient;

    private WireMockServer wireMockServer;

    @BeforeEach
    void setup() {
        wireMockServer = new WireMockServer(githubPort);
        wireMockServer.start();
        configureFor(githubPort);
    }

    @AfterEach
    void teardown() {
        wireMockServer.stop();
    }

    @Test
    void shouldOmitForksWhenGetUsersReposWithoutForks() throws IOException, URISyntaxException {
        String username = "AlmostRenoir";
        stubFor(get(urlEqualTo(getUsersReposGithubUri(username)))
                .withHeader(GitReposService.API_VERSION_HEADER_NAME, equalTo(GitReposService.API_VERSION_HEADER_VALUE))
                .withHeader("Accept", equalTo(GitReposService.GITHUB_JSON_CONTENT_TYPE))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("github/AlmostRenoirRepos.json")));

        stubFor(get(urlEqualTo("/repos/AlmostRenoir/exchange-rate/branches"))
                .withHeader(GitReposService.API_VERSION_HEADER_NAME, equalTo(GitReposService.API_VERSION_HEADER_VALUE))
                .withHeader("Accept", equalTo(GitReposService.GITHUB_JSON_CONTENT_TYPE))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("github/ExchangeRateBranches.json")));

        stubFor(get(urlEqualTo("/repos/AlmostRenoir/repos-browser/branches"))
                .withHeader(GitReposService.API_VERSION_HEADER_NAME, equalTo(GitReposService.API_VERSION_HEADER_VALUE))
                .withHeader("Accept", equalTo(GitReposService.GITHUB_JSON_CONTENT_TYPE))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("github/ReposBrowserBranches.json")));

        String expected = readFileFromResources("gitrepos/AlmostRenoirReposWithoutForks.json");
        webTestClient.get()
                .uri(getUsersReposWithoutForksUri(username))
                .exchange()
                .expectStatus().isOk()
                .expectBody().json(expected);
    }

    @Test
    void shouldReturnEmptyArrayIfUserDoesNotHaveAnyReposWhenGetUsersReposWithoutForks() {
        String username = "Foobar";
        stubFor(get(urlEqualTo(getUsersReposGithubUri(username)))
                .withHeader(GitReposService.API_VERSION_HEADER_NAME, equalTo(GitReposService.API_VERSION_HEADER_VALUE))
                .withHeader("Accept", equalTo(GitReposService.GITHUB_JSON_CONTENT_TYPE))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]")));

        webTestClient.get()
                .uri(getUsersReposWithoutForksUri(username))
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("[]");
    }

    @Test
    void shouldReturn404IfUserNotFoundWhenGetUsersReposWithoutForks() throws IOException, URISyntaxException {
        String username = "Fooobar";
        stubFor(get(urlEqualTo(getUsersReposGithubUri(username)))
                .withHeader(GitReposService.API_VERSION_HEADER_NAME, equalTo(GitReposService.API_VERSION_HEADER_VALUE))
                .withHeader("Accept", equalTo(GitReposService.GITHUB_JSON_CONTENT_TYPE))
                .willReturn(aResponse().withStatus(404)));

        String expected = readFileFromResources("gitrepos/NoUserWithGivenUsername.json");
        webTestClient.get()
                .uri(getUsersReposWithoutForksUri(username))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().json(expected);
    }

    private String getUsersReposGithubUri(String username) {
        return String.format(GitReposService.USERS_REPOS_ROUTE, username);
    }

    private String getUsersReposWithoutForksUri(String username) {
        return String.format(GET_USERS_REPOS_WITHOUT_FORKS_URI, username);
    }

    private String readFileFromResources(String filePath) throws IOException, URISyntaxException {
        URL resourceUrl = getClass().getClassLoader().getResource(filePath);
        if (resourceUrl == null) throw new FileNotFoundException(String.format("%s not found in resources", filePath));
        Path path = Paths.get(resourceUrl.toURI());
        return Files.readString(path, StandardCharsets.UTF_8);
    }

}