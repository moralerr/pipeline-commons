def getPullRequestDetails(Map config = [:]) {
    def response = httpRequest(
        url: "${config.apiUrl}/repos/${config.owner}/${config.repo}/pulls/${config.pullRequestId}",
        httpMode: 'GET',
        headers: [
            Authorization: "Bearer ${config.accessToken}"
        ]
    )

    println response.status
    println response.content

    if (response.status == 200) {
        println "Pull request deatils found."
        return readJSON(text: response.content)
    } else {
        error "Failed to fetch pull request details: ${response.status} - ${response.content}"
    }
}

def mergePullRequest(Map config = [:]) {
    def response = httpRequest(
        url: "${config.apiUrl}/repos/${config.owner}/${config.repo}/pulls/${config.pullRequestId}/merge",
        httpMode: 'PUT',
        headers: [
            Authorization: "Bearer ${config.accessToken}"
            Accept: "application/vnd.github+json"
        ]
    )

    if (response.status == 200 || response.status == 201) {
        return true
    } else {
        error "Failed to merge pull request: ${response.status} - ${response.content}"
    }
}
