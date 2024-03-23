def dockerBuildAndPush(String imageName, String registryUrl, String tag = 'latest', String imageNameOverride = imageName) {
    if (isNullOrEmpty(imageName)) {
        throw new IllegalArgumentException("imageName cannot be null or empty")
    }
    if (isNullOrEmpty(registryUrl)) {
        throw new IllegalArgumentException("registryUrl cannot be null or empty")
    }
    String fullImageName = "${imageName}:${tag}" // Optional
    String registryImageName = "${registryUrl}:${imageNameOverride}-${tag}"

    sh """
    docker build -t ${fullImageName} .
    docker tag ${fullImageName} ${registryImageName}
    docker push ${registryImageName}
    """

    return registryImageName
}

def parseYamlToMap(yamlString) {
    if (yamlString == null || yamlString.trim().isEmpty()) {
        error("YAML string is empty or null.")
        return [:]
    }

    try {
        def parsedMap = yamlSlurper.parseText(yamlString)
        return parsedMap ?: [:]
    } catch (Exception e) {
        error("Failed to parse YAML: ${e.message}")
        return [:]
    }
}

// Helper method for null and empty check
static def isNullOrEmpty(String str) {
    return str == null || str.isEmpty()
}
