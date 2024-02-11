def getPullRequestDetails(Map config = [:]) {
    if (!config.apiUrl || !config.owner || !config.repo || !config.pullRequestId || !config.accessToken) {
        error 'Incomplete configuration provided. Unable to fetch PR deatils.'
    }

    def response = httpRequest(
        url: "${config.apiUrl}/repos/${config.owner}/${config.repo}/pulls/${config.pullRequestId}",
        httpMode: 'GET',
        customHeaders: [
            [name: 'Authorization', value: "Bearer ${config.accessToken}", maskValue: true]
        ]
    )

    if (response.status == 200) {
        println 'Pull request details found.'
        return readJSON(text: response.content)
    } else {
        error "Failed to fetch pull request details: ${response.status} - ${response.content}"
    }
}

def mergePullRequest(Map config = [:]) {
    if (!config.apiUrl || !config.owner || !config.repo || !config.pullRequestId || !config.accessToken) {
        error 'Incomplete configuration provided. Unable to merge PR.'
    }

    def response = httpRequest(
        url: "${config.apiUrl}/repos/${config.owner}/${config.repo}/pulls/${config.pullRequestId}/merge",
        httpMode: 'PUT',
        customHeaders: [
            [name: 'Authorization', value: "Bearer ${config.accessToken}", maskValue: true],
            [name: 'Accept', value: 'application/vnd.github+json'],
        ]
    )

    if (response.status == 200 || response.status == 201) {
        return true
    } else {
        error "Failed to merge pull request: ${response.status} - ${response.content}"
    }
}

