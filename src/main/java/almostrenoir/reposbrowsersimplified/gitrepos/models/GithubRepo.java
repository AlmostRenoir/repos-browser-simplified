package almostrenoir.reposbrowsersimplified.gitrepos.models;

public record GithubRepo(String name, GithubRepoOwner owner, boolean fork, String branches_url) {
    public record GithubRepoOwner(String login) { }

    public String getCorrectBranchesUrl() {
        return branches_url.replace("{/branch}", "");
    }
}


