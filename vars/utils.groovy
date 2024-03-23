def dockerBuild(String imageName, String tag = 'latest', String registryUrl, String imageNameOverride = imageName) {
    if (isNullOrEmpty(imageName)) {
        throw new IllegalArgumentException("imageName cannot be null or empty")
    }
    if (isNullOrEmpty(registryUrl)) {
        throw new IllegalArgumentException("registryUrl cannot be null or empty")
    }
    String fullImageName = "${imageName}:${tag}" // Optional
    String registryImageName = "${registryUrl}/${imageNameOverride}-${tag}"

    sh """
    docker build -t ${fullImageName} .
    docker tag ${fullImageName} ${registryImageName}
    """

    return registryImageName
}

def dockerPush() {

    sh "docker push ${REGISTRY_NAME}/${REGISTRY_REPO}:${IMAGE_NAME}-${IMAGE_VERSION}"

}

def mergeYamlFiles(argumentYamlFile) {
    def defaultYaml = new File('default.yaml').text // Read default YAML content
    def argumentYaml = new File(argumentYamlFile).text // Read argument YAML content

    // Function to parse YAML string to a Groovy map
    def parseYamlToMap(String yamlString) {
        def yamlMap = new Expando() // Create an expandable map
        yamlString.split("\n").findAll { it.trim() }.each { line ->
            def (key, value) = line.split(":").collect { it.trim() }
            if (value.startsWith("---")) {
                return // Skip document separators (for multi-doc YAML)
            }
            if (value.startsWith("- ")) {
                // Handle list entries
                def existingList = yamlMap.get(key) ?: []
                existingList << value.substring(2).trim()
                yamlMap[key] = existingList
            } else {
                yamlMap[key] = value
            }
        }
        return yamlMap
    }

    // Merge maps, giving precedence to argumentYaml
    def mergedMap = new Expando()
    mergedMap.putAll(parseYamlToMap(defaultYaml))
    mergedMap.putAll(parseYamlToMap(argumentYaml))

    return mergedMap
}

// Helper method for null and empty check
static def isNullOrEmpty(String str) {
    return str == null || str.isEmpty()
}
