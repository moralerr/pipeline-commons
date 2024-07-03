def dockerBuild(String imageName, String tag = 'latest', String registryUrl, String imageNameOverride = imageName) {
    if (isNullOrEmpty(imageName)) {
        throw new IllegalArgumentException("imageName cannot be null or empty")
    }
    if (isNullOrEmpty(registryUrl)) {
        throw new IllegalArgumentException("registryUrl cannot be null or empty")
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
        throw new IllegalArgumentException("All parameters must be provided and cannot be null or empty")
    }

    sh "docker push ${registryName}/${registryRepo}:${imageName}-${imageVersion}"
}

def mergeYamlFiles(String argumentYamlFile) {
    def defaultYaml = new File('default.yaml').text
    def argumentYaml = new File(argumentYamlFile).text

    def parseYamlToMap(String yamlString) {
        def yamlMap = [:]
        yamlString.split("\n").findAll { it.trim() }.each { line ->
            def (key, value) = line.split(":").collect { it.trim() }
            if (value?.startsWith("---")) {
                return
            }
            if (value?.startsWith("- ")) {
                yamlMap[key] = yamlMap.getOrDefault(key, []) + value.substring(2).trim()
            } else {
                yamlMap[key] = value
            }
        }
        return yamlMap
    }

    def mergedMap = parseYamlToMap(defaultYaml)
    mergedMap.putAll(parseYamlToMap(argumentYaml))

    return mergedMap
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

