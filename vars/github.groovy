def getPullRequestDetails(Map config = [:]) {
    if (!config.apiUrl || !config.owner || !config.repo || !config.pullRequestId || !config.accessToken) {
        error 'Incomplete configuration provided. Unable to fetch PR details.'
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

def getRepoDetails(Map config = [:]) {
    if (!config.apiUrl || !config.owner || !config.repo || !config.accessToken) {
        error 'Incomplete configuration provided. Unable to fetch repository details.'
    }

    def response = httpRequest(
        url: "${config.apiUrl}/repos/${config.owner}/${config.repo}",
        httpMode: 'GET',
        customHeaders: [
            [name: 'Authorization', value: "Bearer ${config.accessToken}", maskValue: true]
        ]
    )

    if (response.status == 200) {
        println 'Repository details found.'
        return readJSON(text: response.content)
    } else {
        error "Failed to fetch repository details: ${response.status} - ${response.content}"
    }
}

def createIssue(Map config = [:]) {
    if (!config.apiUrl || !config.owner || !config.repo || !config.title || !config.body || !config.accessToken) {
        error 'Incomplete configuration provided. Unable to create issue.'
    }

    def response = httpRequest(
        url: "${config.apiUrl}/repos/${config.owner}/${config.repo}/issues",
        httpMode: 'POST',
        contentType: 'APPLICATION_JSON',
        customHeaders: [
            [name: 'Authorization', value: "Bearer ${config.accessToken}", maskValue: true],
            [name: 'Accept', value: 'application/vnd.github+json'],
        ],
        requestBody: writeJSON(returnText: true, json: [
            title: config.title,
            body: config.body
        ])
    )

    if (response.status == 201) {
        println 'Issue created successfully.'
        return readJSON(text: response.content)
    } else {
        error "Failed to create issue: ${response.status} - ${response.content}"
    }
}

def closeIssue(Map config = [:]) {
    if (!config.apiUrl || !config.owner || !config.repo || !config.issueNumber || !config.accessToken) {
        error 'Incomplete configuration provided. Unable to close issue.'
    }

    def response = httpRequest(
        url: "${config.apiUrl}/repos/${config.owner}/${config.repo}/issues/${config.issueNumber}",
        httpMode: 'PATCH',
        contentType: 'APPLICATION_JSON',
        customHeaders: [
            [name: 'Authorization', value: "Bearer ${config.accessToken}", maskValue: true],
            [name: 'Accept', value: 'application/vnd.github+json'],
        ],
        requestBody: writeJSON(returnText: true, json: [
            state: 'closed'
        ])
    )

    if (response.status == 200) {
        println 'Issue closed successfully.'
        return readJSON(text: response.content)
    } else {
        error "Failed to close issue: ${response.status} - ${response.content}"
    }
}

def listIssues(Map config = [:]) {
    if (!config.apiUrl || !config.owner || !config.repo || !config.accessToken) {
        error 'Incomplete configuration provided. Unable to list issues.'
    }

    def response = httpRequest(
        url: "${config.apiUrl}/repos/${config.owner}/${config.repo}/issues",
        httpMode: 'GET',
        customHeaders: [
            [name: 'Authorization', value: "Bearer ${config.accessToken}", maskValue: true]
        ]
    )

    if (response.status == 200) {
        println 'Issues retrieved successfully.'
        return readJSON(text: response.content)
    } else {
        error "Failed to list issues: ${response.status} - ${response.content}"
    }
}

def createBranch(Map config = [:]) {
    if (!config.apiUrl || !config.owner || !config.repo || !config.branchName || !config.baseBranch || !config.accessToken) {
        error 'Incomplete configuration provided. Unable to create branch.'
    }

    def response = httpRequest(
        url: "${config.apiUrl}/repos/${config.owner}/${config.repo}/git/refs",
        httpMode: 'POST',
        contentType: 'APPLICATION_JSON',
        customHeaders: [
            [name: 'Authorization', value: "Bearer ${config.accessToken}", maskValue: true],
            [name: 'Accept', value: 'application/vnd.github+json'],
        ],
        requestBody: writeJSON(returnText: true, json: [
            ref: "refs/heads/${config.branchName}",
            sha: getBaseBranchSHA(config)
        ])
    )

    if (response.status == 201) {
        println 'Branch created successfully.'
        return readJSON(text: response.content)
    } else {
        error "Failed to create branch: ${response.status} - ${response.content}"
    }
}

def getBaseBranchSHA(Map config = [:]) {
    def response = httpRequest(
        url: "${config.apiUrl}/repos/${config.owner}/${config.repo}/git/refs/heads/${config.baseBranch}",
        httpMode: 'GET',
        customHeaders: [
            [name: 'Authorization', value: "Bearer ${config.accessToken}", maskValue: true]
        ]
    )

    if (response.status == 200) {
        def branchData = readJSON(text: response.content)
        return branchData.object.sha
    } else {
        error "Failed to get base branch SHA: ${response.status} - ${response.content}"
    }
}

def listBranches(Map config = [:]) {
    if (!config.apiUrl || !config.owner || !config.repo || !config.accessToken) {
        error 'Incomplete configuration provided. Unable to list branches.'
    }

    def response = httpRequest(
        url: "${config.apiUrl}/repos/${config.owner}/${config.repo}/branches",
        httpMode: 'GET',
        customHeaders: [
            [name: 'Authorization', value: "Bearer ${config.accessToken}", maskValue: true]
        ]
    )

    if (response.status == 200) {
        println 'Branches retrieved successfully.'
        return readJSON(text: response.content)
    } else {
        error "Failed to list branches: ${response.status} - ${response.content}"
    }
}

def getBranchDetails(Map config = [:]) {
    if (!config.apiUrl || !config.owner || !config.repo || !config.branchName || !config.accessToken) {
        error 'Incomplete configuration provided. Unable to fetch branch details.'
    }

    def response = httpRequest(
        url: "${config.apiUrl}/repos/${config.owner}/${config.repo}/branches/${config.branchName}",
        httpMode: 'GET',
        customHeaders: [
            [name: 'Authorization', value: "Bearer ${config.accessToken}", maskValue: true]
        ]
    )

    if (response.status == 200) {
        println 'Branch details found.'
        return readJSON(text: response.content)
    } else {
        error "Failed to fetch branch details: ${response.status} - ${response.content}"
    }
}

def deleteBranch(Map config = [:]) {
    if (!config.apiUrl || !config.owner || !config.repo || !config.branchName || !config.accessToken) {
        error 'Incomplete configuration provided. Unable to delete branch.'
    }

    def response = httpRequest(
        url: "${config.apiUrl}/repos/${config.owner}/${config.repo}/git/refs/heads/${config.branchName}",
        httpMode: 'DELETE',
        customHeaders: [
            [name: 'Authorization', value: "Bearer ${config.accessToken}", maskValue: true]
        ]
    )

    if (response.status == 204) {
        println 'Branch deleted successfully.'
        return true
    } else {
        error "Failed to delete branch: ${response.status} - ${response.content}"
    }
}

def createRelease(Map config = [:]) {
    if (!config.apiUrl || !config.owner || !config.repo || !config.tagName || !config.releaseName || !config.releaseBody || !config.accessToken) {
        error 'Incomplete configuration provided. Unable to create release.'
    }

    def response = httpRequest(
        url: "${config.apiUrl}/repos/${config.owner}/${config.repo}/releases",
        httpMode: 'POST',
        contentType: 'APPLICATION_JSON',
        customHeaders: [
            [name: 'Authorization', value: "Bearer ${config.accessToken}", maskValue: true],
            [name: 'Accept', value: 'application/vnd.github+json'],
        ],
        requestBody: writeJSON(returnText: true, json: [
            tag_name: config.tagName,
            name: config.releaseName,
            body: config.releaseBody
        ])
    )

    if (response.status == 201) {
        println 'Release created successfully.'
        return readJSON(text: response.content)
    } else {
        error "Failed to create release: ${response.status} - ${response.content}"
    }
}

def listReleases(Map config = [:]) {
    if (!config.apiUrl || !config.owner || !config.repo || !config.accessToken) {
        error 'Incomplete configuration provided. Unable to list releases.'
    }

    def response = httpRequest(
        url: "${config.apiUrl}/repos/${config.owner}/${config.repo}/releases",
        httpMode: 'GET',
        customHeaders: [
            [name: 'Authorization', value: "Bearer ${config.accessToken}", maskValue: true]
        ]
    )

    if (response.status == 200) {
        println 'Releases retrieved successfully.'
        return readJSON(text: response.content)
    } else {
        error "Failed to list releases: ${response.status} - ${response.content}"
    }
}
