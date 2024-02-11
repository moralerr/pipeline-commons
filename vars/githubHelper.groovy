// vars/githubHelper.groovy

class GitHubHelper {

    String apiUrl
    String accessToken

    GitHubHelper(String apiUrl, String accessToken) {
        this.apiUrl = apiUrl
        this.accessToken = accessToken
    }

    def getPullRequestDetails(String owner, String repo, int pullRequestId) {
        def response = httpRequest(
            url: "${apiUrl}/repos/${owner}/${repo}/pulls/${pullRequestId}",
            httpMode: 'GET',
            headers: [
                Authorization: "Bearer ${accessToken}"
            ]
        )

        if (response.status == 200) {
            return readJSON(response.content)
        } else {
            error "Failed to fetch pull request details: ${response.status} - ${response.content}"
        }
    }

    def mergePullRequest(String owner, String repo, int pullRequestId) {
        def response = httpRequest(
            url: "${apiUrl}/repos/${owner}/${repo}/pulls/${pullRequestId}/merge",
            httpMode: 'PUT',
            headers: [
                Authorization: "Bearer ${accessToken}"
            ],
            requestBody: '{"merge_method": "merge"}'
        )

        if (response.status == 200 || response.status == 201) {
            return true
        } else {
            error "Failed to merge pull request: ${response.status} - ${response.content}"
        }
    }

}
