package almostrenoir.reposbrowsersimplified.gitrepos.models;

public record GithubBranch(String name, GithubCommit commit) {
    public record GithubCommit(String sha) {}
}
