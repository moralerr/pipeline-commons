# Jenkins Pipeline Commons

This repository contains a set of utility functions to be used in Jenkins pipelines for managing Docker images and Kubernetes resources. These utilities are particularly useful for CI/CD setups and can help automate various tasks related to Docker and Kubernetes.

## Functions

### Docker Functions

#### `dockerBuild`
Builds and tags a Docker image.

```groovy
def dockerBuild(String imageName, String tag = 'latest', String registryUrl, String imageNameOverride = imageName)
```

**Parameters:**
- `imageName` (String): Name of the Docker image.
- `tag` (String): Tag for the Docker image (default: 'latest').
- `registryUrl` (String): URL of the Docker registry.
- `imageNameOverride` (String): Override name for the Docker image (default: `imageName`).

#### `dockerPush`
Pushes a Docker image to a registry.

```groovy
def dockerPush(String registryName, String registryRepo, String imageName, String imageVersion)
```

**Parameters:**
- `registryName` (String): Name of the Docker registry.
- `registryRepo` (String): Repository in the Docker registry.
- `imageName` (String): Name of the Docker image.
- `imageVersion` (String): Version tag of the Docker image.

#### `dockerImageExists`
Checks if a Docker image exists locally.

```groovy
def dockerImageExists(String imageName, String tag = 'latest')
```

**Parameters:**
- `imageName` (String): Name of the Docker image.
- `tag` (String): Tag for the Docker image (default: 'latest').

#### `dockerPull`
Pulls a Docker image from a registry.

```groovy
def dockerPull(String imageName, String tag = 'latest')
```

**Parameters:**
- `imageName` (String): Name of the Docker image.
- `tag` (String): Tag for the Docker image (default: 'latest').

#### `dockerRemoveImage`
Removes a Docker image locally.

```groovy
def dockerRemoveImage(String imageName, String tag = 'latest')
```

**Parameters:**
- `imageName` (String): Name of the Docker image.
- `tag` (String): Tag for the Docker image (default: 'latest').

### Kubernetes Functions

#### `buildKubernetesManifest`
Builds a Kubernetes manifest file from a template.

```groovy
def buildKubernetesManifest(String templateFile, Map params, String outputFile)
```

**Parameters:**
- `templateFile` (String): Path to the Kubernetes template file.
- `params` (Map): Parameters to replace in the template.
- `outputFile` (String): Path to save the generated manifest file.

#### `applyKubernetesManifest`
Applies a Kubernetes manifest file.

```groovy
def applyKubernetesManifest(String manifestFile)
```

**Parameters:**
- `manifestFile` (String): Path to the Kubernetes manifest file.

#### `deleteKubernetesResource`
Deletes a Kubernetes resource.

```groovy
def deleteKubernetesResource(String resourceType, String resourceName)
```

**Parameters:**
- `resourceType` (String): Type of the Kubernetes resource (e.g., pod, service, deployment).
- `resourceName` (String): Name of the Kubernetes resource.

#### `getKubernetesPodLogs`
Gets logs from a Kubernetes pod.

```groovy
def getKubernetesPodLogs(String podName, String containerName = '')
```

**Parameters:**
- `podName` (String): Name of the Kubernetes pod.
- `containerName` (String): Name of the container in the pod (optional).

#### `scaleKubernetesDeployment`
Scales a Kubernetes deployment.

```groovy
def scaleKubernetesDeployment(String deploymentName, int replicas)
```

**Parameters:**
- `deploymentName` (String): Name of the Kubernetes deployment.
- `replicas` (int): Number of replicas to scale to.

### Utility Functions

#### `mergeYamlFiles`
Merges two YAML files.

```groovy
def mergeYamlFiles(String argumentYamlFile)
```

**Parameters:**
- `argumentYamlFile` (String): Path to the YAML file to merge with the default.

#### `isNullOrEmpty`
Checks if a string is null or empty.

```groovy
static def isNullOrEmpty(String str)
```

**Parameters:**
- `str` (String): The string to check.

## Usage

To use these utility functions in your Jenkins pipeline, you can load the script and call the functions as needed.

```groovy
@Library('jenkins-pipeline-utils') _
import jenkins.pipeline.utils.*

node {
    stage('Build Docker Image') {
        def imageName = dockerBuild('my-app', '1.0.0', 'my-registry.com')
        echo "Docker image built: ${imageName}"
    }

    stage('Push Docker Image') {
        dockerPush('my-registry.com', 'my-repo', 'my-app', '1.0.0')
        echo "Docker image pushed."
    }

    stage('Deploy to Kubernetes') {
        def manifestFile = buildKubernetesManifest('deployment-template.yaml', [IMAGE_TAG: '1.0.0'], 'deployment.yaml')
        applyKubernetesManifest(manifestFile)
        echo "Kubernetes manifest applied."
    }
}
```

## Contributing

Contributions are welcome! Please open an issue or submit a pull request with your changes.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.