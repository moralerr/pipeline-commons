import groovy.json.JsonOutput
import groovy.json.JsonSlurper

def dockerBuild(String imageName, String tag = 'latest', String registryUrl, String imageNameOverride = imageName) {
    if (isNullOrEmpty(imageName)) {
        throw new IllegalArgumentException('imageName cannot be null or empty')
    }
    if (isNullOrEmpty(registryUrl)) {
        throw new IllegalArgumentException('registryUrl cannot be null or empty')
    }
    String fullImageName = "${imageName}:${tag}"
    String registryImageName = "${registryUrl}/${imageNameOverride}-${tag}"

    sh """
    docker build -t ${fullImageName} .
    docker tag ${fullImageName} ${registryImageName}
    """

    return registryImageName
}

def dockerPush(String registryName, String registryRepo, String imageName, String imageVersion) {
    if (isNullOrEmpty(registryName) || isNullOrEmpty(registryRepo) || isNullOrEmpty(imageName) || isNullOrEmpty(imageVersion)) {
        throw new IllegalArgumentException('All parameters must be provided and cannot be null or empty')
    }

    sh "docker push ${registryName}/${registryRepo}:${imageName}-${imageVersion}"
}

static def isNullOrEmpty(String str) {
    return str == null || str.isEmpty()
}

def dockerImageExists(String imageName, String tag = 'latest') {
    def result = sh(script: "docker images -q ${imageName}:${tag}", returnStdout: true).trim()
    return !isNullOrEmpty(result)
}

def dockerPull(String imageName, String tag = 'latest') {
    sh "docker pull ${imageName}:${tag}"
}

def dockerRemoveImage(String imageName, String tag = 'latest') {
    sh "docker rmi ${imageName}:${tag}"
}

def getLatestJenkinsHelmChartVersion() {
    def response = httpRequest(
        url: 'https://api.github.com/repos/jenkinsci/helm-charts/releases/latest',
        httpMode: 'GET',
        customHeaders: [
            [name: 'Accept', value: 'application/vnd.github.v3+json']
        ]
    )

    if (response.status == 200) {
        def json = new JsonSlurper().parseText(response.content)
        def latestVersion = json.tag_name.replaceAll('^v', '') // Remove the 'v' prefix if present
        if (latestVersion.startsWith('jenkins-')) {
            latestVersion = latestVersion.replaceFirst(/^jenkins-/, '')
        }
        return latestVersion
    } else {
        error "Failed to fetch latest Jenkins Helm chart version: ${response.status} - ${response.content}"
    }
}

def getCurrentHelmChartInfo(String repoOwner, String repoName, String filePath, String accessToken) {
    filePath = filePath.trim()
    def response = httpRequest(
        url: "https://api.github.com/repos/${repoOwner}/${repoName}/contents/${filePath}",
        httpMode: 'GET',
        customHeaders: [
            [name: 'Authorization', value: "Bearer ${accessToken}", maskValue: true],
            [name: 'Accept', value: 'application/vnd.github.v3+json']
        ]
    )

    if (response.status == 200) {
        def json = new JsonSlurper().parseText(response.content)
        def fileContent = new String(json.content.decodeBase64())
        def versionMatcher = fileContent =~ /version:\s*(.*)/
        def dependencyMatcher = fileContent =~ /- name: jenkins\s*version:\s*(.*)/
        if (versionMatcher.find() && dependencyMatcher.find()) {
            return [
                chartVersion: versionMatcher.group(1).trim(),
                dependencyVersion: dependencyMatcher.group(1).trim()
            ]
        } else {
            error 'Failed to extract versions from Chart.yaml'
        }
    } else {
        error "Failed to fetch Chart.yaml: ${response.status} - ${response.content}"
    }
}

def updateHelmChartInfo(String filePath, String newVersion, String newDependencyVersion) {
    filePath = filePath.trim()
    println "Updating file at path: ${filePath}"
    def content = readFile(filePath)
    content = content.replaceFirst(/version:\s*.*/, "version: ${newVersion}")
    content = content.replaceFirst(/- name: jenkins\s*version:\s*.*/, "- name: jenkins\n  version: ${newDependencyVersion}")
    writeFile(file: filePath, text: content)
}

def incrementMinorVersion(String version) {
    def (major, minor, patch) = version.tokenize('.').collect { it.toInteger() }
    return "${major}.${minor + 1}.0" // Reset patch version to 0
}

def createPullRequest(Map config) {
    def jsonPayload = JsonOutput.toJson([
        title: config.title,
        head: config.headBranch,
        base: config.baseBranch,
        body: config.body
    ])

    def response = httpRequest(
        url: "${config.apiUrl}/repos/${config.owner}/${config.repo}/pulls",
        httpMode: 'POST',
        customHeaders: [
            [name: 'Authorization', value: "Bearer ${config.accessToken}", maskValue: true],
            [name: 'Accept', value: 'application/vnd.github.v3+json']
        ],
        requestBody: jsonPayload
    )

    if (response.status == 201) {
        println 'Pull request created successfully.'
        return new JsonSlurper().parseText(response.content)
    } else {
        error "Failed to create pull request: ${response.status} - ${response.content}"
    }
}

// Function to replace template strings in YAML content
def replaceTemplateStringsInYamlContent(String yamlContent) {
    echo 'test'
}

def logMapReadable(Map map, int indentLevel = 0) {
    map.each { key, value ->
        def indent = '  ' * indentLevel
        if (value instanceof Map) {
            println "${indent}${key}:"
            logMapReadable(value, indentLevel + 1)
        } else if (value instanceof List) {
            println "${indent}${key}:"
            value.each { item ->
                if (item instanceof Map || item instanceof List) {
                    println "${indent}  -"
                    logMapReadable(item, indentLevel + 2)
                } else {
                    println "${indent}  - ${item}"
                }
            }
        } else {
            println "${indent}${key}: ${value}"
        }
    }
}
