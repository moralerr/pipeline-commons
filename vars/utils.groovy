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

    def parsedMap = [:]
    def lines = yamlString.split('\n')
    def currentMap = parsedMap
    def stack = []

    lines.each { line ->
        def indent = line.takeWhile { it == ' ' }.size()
        def keyValuePair = line.trim()

        if (keyValuePair) {
            def key = keyValuePair.takeWhile { it != ':' }
            def value = keyValuePair.dropWhile { it != ':' }.drop(1).trim()

            if (indent == 0) {
                parsedMap[key] = value == '' ? [:] : value
                currentMap = parsedMap
                stack = []
            } else {
                while (stack.size() >= indent) {
                    stack.pop()
                }
                stack << key
                currentMap = stack.inject(parsedMap) { map, k -> map[k] }
                currentMap[key] = value == '' ? [:] : value
            }
        }
    }

    return parsedMap
}


// Helper method for null and empty check
static def isNullOrEmpty(String str) {
    return str == null || str.isEmpty()
}
