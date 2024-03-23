def dockerBuild(String imageName, String registryUrl, String tag = 'latest', String imageNameOverride = imageName) {
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

// Helper method for null and empty check
static def isNullOrEmpty(String str) {
    return str == null || str.isEmpty()
}
