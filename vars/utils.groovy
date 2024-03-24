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

Map merge(Map[] sources) {
    if (!sources) return [:]
    if (sources.length == 1) return sources[0]
    sources.inject([:]) { result, map ->
        map.each { key, value ->
            result[key] = (result[key] instanceof Map && value instanceof Map) ? merge(result[key], value) : value
        }
        result
    }
}

Map expandMap(Map source, String delimiter = '.') {
    source?.inject([:]) { result, key, value ->
        if (value instanceof Map) value = expandMap(value)
        if (value instanceof Collection) value = value.collect { it instanceof Map ? expandMap(it) : it }
        merge(result, key.toString().tokenize(delimiter).reverse().inject(value) { current, token -> [(token): current] })
    }
}

Object injectValues(Object source, Map values) {
    if (source instanceof Map) {
        source.collectEntries { key, value -> [(injectValues(key, values)): injectValues(value, values)] }
    } else if (source instanceof List) {
        source.collect { injectValues(it, values) }
    } else if (source instanceof String && source.contains('$')) {
        def placeholder = source =~ /^\$\{([^}]+)}$/
        if (placeholder.find()) {
            evaluateOrDefault(placeholder.group(1), values, placeholder.group())
        } else {
            source.replaceAll(~/\$\{([^}]+)}/) { evaluateOrDefault(it[1], values, it[0]) }
        }
    } else {
        source
    }
}

Object evaluateOrDefault(String expression, Map values, Object defaultValue) {
    try {
        def replaced = new GroovyShell(new Binding(values)).evaluate(expression)
        (replaced != null) ? replaced : defaultValue
    } catch (NullPointerException e) {
        defaultValue
    } catch (e) {
        log.debug('Ignored exception parsing expression "{}": {}', expression, e.getMessage())
        defaultValue
    }
}



// Helper method for null and empty check
static def isNullOrEmpty(String str) {
    return str == null || str.isEmpty()
}
