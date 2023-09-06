package almostrenoir.reposbrowsersimplified.gitrepos.models;

import java.util.List;

public record GitRepoDTO(String name, String owner, List<GitBranchDTO> branches) {
    public record GitBranchDTO(String name, String lastCommitSHA) {}

    public static GitRepoDTO fromGithubData(GithubRepo repo, List<GithubBranch> branches) {
        return new GitRepoDTO(
                repo.name(),
                repo.owner().login(),
                branches.stream().map(branch -> new GitBranchDTO(branch.name(), branch.commit().sha())).toList()
        );
    }
}
